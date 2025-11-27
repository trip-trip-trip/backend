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

        // (trip, creator) 기준으로 가장 최신 ShortReel 찾기
        ShortReel reel = shortReelRepository
                .findFirstByTrip_IdAndCreator_IdOrderByCreatedAtDesc(req.tripId(), loggedInUserId)
                .orElseGet(() -> {
                    Trip trip = tripRepository.findById(req.tripId())
                            .orElseThrow(EntityNotFoundException::new);
                    User creator = userRepository.findById(loggedInUserId)
                            .orElseThrow(EntityNotFoundException::new);

                    // 이 시점에서부터 creator별로 서로 다른 reel 생성 (outputMedia 비어있고 COLLECTING 상태)
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

        // 위치 계산 + item 생성 부분은 그대로
        MediaAssetRes storedMedia = mediaAssetService.createMediaAssetFromMultipart(file, req.media(), ContentType.VIDEO);
        Integer position = shortReelItemRepository.findByReel_Id(reel.getId())
                .stream().map(ShortReelItem::getPosition)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;

        ShortReelItem data = shortReelItemRepository.save(
                ShortReelItem.builder()
                        .reel(reel)
                        .position(position)
                        .holdMs(3)
                        .transition(ReelTransition.NONE)
                        .media(
                                mediaAssetRepository.findById(storedMedia.mediaAssetId())
                                        .orElseThrow(() -> new EntityNotFoundException("no media asset"))
                        )
                        .build()
        );

        return new ReelItemRes(data.getId(), req.tripId(), data.getPosition(), storedMedia);
    }

    @Transactional
    public ReelStatusRes getOrQueueWhenEnded(Long tripId) throws IOException {
        Long loggedInUserId = currentUserProvider.getUserId()
                .orElseThrow(() -> new EntityNotFoundException("User not logged in"));

        Trip trip = tripRepository.findById(tripId).orElseThrow();

        // (trip, creator) 기준으로 현재 유저의 리일만 조회
        Optional<ShortReel> opt = shortReelRepository
                .findFirstByTrip_IdAndCreator_IdOrderByCreatedAtDesc(tripId, loggedInUserId);

        if (opt.isEmpty()) {
            // 이 유저는 아직 이 trip에 대해 리일을 만든 적이 없음
            // → 기존 로직 유지: "클립이 아직 없습니다" 정도를 보여줄 수 있게 NONE 반환
            return new ReelStatusRes("NONE", null, null);
        }

        ShortReel reel = opt.get();

        // 여행 종료 여부
        boolean ended = trip.getStatus() == TripStatus.COMPLETED
                || (trip.getEndDate() != null && trip.getEndDate().isBefore(LocalDate.now()));

        // 여행이 끝났고, 아직 결과물이 없는 상태면 → 큐잉
        if (ended && reel.getOutputMedia() == null &&
                (reel.getRenderStatus() == ReelRenderStatus.COLLECTING
                        || reel.getRenderStatus() == ReelRenderStatus.FAILED)) {

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
