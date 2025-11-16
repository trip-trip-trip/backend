package yeohaenggasijo.tripshot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yeohaenggasijo.tripshot.domain.common.*;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.reel.ShortReel;
import yeohaenggasijo.tripshot.domain.reel.ShortReelItem;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.dto.reel.CreateReelItemReq;
import yeohaenggasijo.tripshot.dto.reel.ReelItemRes;
import yeohaenggasijo.tripshot.dto.reel.ReelRes;
import yeohaenggasijo.tripshot.dto.reel.ReelStatusRes;
import yeohaenggasijo.tripshot.repository.*;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.storage.RenderService;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortReelService {
    private final MediaAssetRepository mediaAssetRepository;
    private final UserRepository userRepository;
    private final ShortReelRepository shortReelRepository;
    private final TripRepository tripRepository;
    private final CurrentUserProvider currentUserProvider;
    private final MediaAssetService mediaAssetService;
    private final ShortReelItemRepository shortReelItemRepository;
    private final RenderService renderService;

    @Transactional
    public ReelItemRes createReelItem(MultipartFile file, CreateReelItemReq req) {
        Long loggedInUserId = currentUserProvider.getUserId()
                .orElseThrow(() -> new EntityNotFoundException("User not logged in"));
        CreateMediaAssetReq media = req.media();
        ShortReel reel = shortReelRepository.findByTrip_Id(req.tripId())
                .orElseGet(() -> {
                    Trip trip = tripRepository.findById(req.tripId())
                            .orElseThrow(EntityNotFoundException::new);
                    User creator = userRepository.findById(loggedInUserId)
                            .orElseThrow(EntityNotFoundException::new);

                    return shortReelRepository.save(
                            ShortReel.builder()
                                    .trip(trip)
                                    .title(trip.getTitle() + "의 기억")
                                    .creator(creator)
                                    .outputMedia(null)
                                    .renderStatus(ReelRenderStatus.COLLECTING)
                                    .build()
                    );
                });

        // 위치 계산(현재 trip이 가진 shortReel의 모든 item중 가장 큰 position + 1, 없으면 1)
        MediaAssetRes storedMedia = mediaAssetService.createMediaAssetFromMultipart(file, req.media(), ContentType.VIDEO);
        Integer position = shortReelItemRepository.findByReel_Id(reel.getId())
                .stream().map(ShortReelItem::getPosition)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;

        //media asset은 따로 서비스 호출해서 저장하고 옴(res 반환하면 거기서 id로 리포에서 찾아서 reel에 연결 저장)
        ShortReelItem data = shortReelItemRepository.save(ShortReelItem.builder()
                .reel(reel)
                .position(position)
                .holdMs(3)
                .transition(ReelTransition.NONE)
                .media(mediaAssetRepository.findById(storedMedia.mediaAssetId()).orElseThrow(() -> new EntityNotFoundException("no media asset")))
                .build());

//        ShortReel data = shortReelRepository.save(ShortReel.builder()
//                .title(req.title())
//                .trip(tripRepository.findById(media.tripId()).orElseThrow(() -> new EntityNotFoundException("no trip")))
//                .creator(userRepository.findById(loggedInUserId).orElseThrow(() -> new EntityNotFoundException("no user")))
//                .renderStatus(ReelRenderStatus.DONE)
//                .outputMedia(mediaAssetRepository.findById(storedMedia.mediaAssetId()).orElseThrow(() -> new EntityNotFoundException("no media asset")))
//                .build()
//        );
        return new ReelItemRes(data.getId(), req.tripId(), data.getPosition(), storedMedia);
    }

    @Transactional
    public ReelStatusRes getOrQueueWhenEnded(Long tripId) throws IOException {
        Trip trip = tripRepository.findById(tripId).orElseThrow();

        Optional<ShortReel> opt = shortReelRepository.findByTrip_Id(tripId);
        if (opt.isEmpty()) {
            // 아직 클립이 한 개도 안 올라온 상태(= reel 자체가 없음)
            // 화면에는 "클립이 아직 없습니다" 정도만 내려주면 OK
            return new ReelStatusRes("NONE", null, null);
        }

        ShortReel reel = opt.get();

        // 여행이 끝났고 아직 결과물이 없다면 → 큐잉
        boolean ended = trip.getStatus() == TripStatus.COMPLETED
                || (trip.getEndDate() != null && trip.getEndDate().isBefore(LocalDate.now()));
        if (ended && reel.getOutputMedia() == null &&
                (reel.getRenderStatus() == ReelRenderStatus.COLLECTING || reel.getRenderStatus() == ReelRenderStatus.FAILED)) {
            reel.setRenderStatus(ReelRenderStatus.QUEUED);
            shortReelRepository.save(reel);

            renderService.renderAsync(reel.getId()); // 비동기 렌더 시작
            return new ReelStatusRes("QUEUED", reel.getId(), null);
        }

        // 그 외(진행중/완료/실패 등) 그대로 상태 반환
        String url = (reel.getOutputMedia() != null) ? reel.getOutputMedia().getUrl() : null;
        return new ReelStatusRes(reel.getRenderStatus().name(), reel.getId(), url);
    }
}
