package yeohaenggasijo.tripshot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.common.*;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.reel.ShortReel;
import yeohaenggasijo.tripshot.domain.reel.ShortReelItem;
import yeohaenggasijo.tripshot.domain.scrapbook.Scrapbook;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.trip.TripParticipant;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.dto.photo.PhotoRes;
import yeohaenggasijo.tripshot.dto.reel.ReelItemRes;
import yeohaenggasijo.tripshot.dto.reel.ReelRes;
import yeohaenggasijo.tripshot.dto.scrapbook.ScrapbookRes;
import yeohaenggasijo.tripshot.dto.trip.req.TripCreateReq;
import yeohaenggasijo.tripshot.dto.trip.res.TripDetailRes;
import yeohaenggasijo.tripshot.dto.trip.res.TripMediaRes;
import yeohaenggasijo.tripshot.dto.trip.res.TripRes;
import yeohaenggasijo.tripshot.exception.BadRequestException;
import yeohaenggasijo.tripshot.repository.*;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripInvitationRepository tripInvitationRepository;
    private final Clock clock;
    private final MediaAssetRepository mediaAssetRepository;
    private final ShortReelItemRepository shortReelItemRepository;
    private final ShortReelRepository shortReelRepository;
    private final ScrapbookRepository scrapbookRepository;
    private final PlaceRepository placeRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private static final Logger logger = LoggerFactory.getLogger(TripService.class);


    @Transactional
    public Trip create(Long uid, TripCreateReq req) {
        User owner = userRepository.findById(uid)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
        LocalDate today = LocalDate.now(clock);
        if (req.endDate().isBefore(today)) {
            throw new BadRequestException("End date already passed");
        }
        TripStatus status = req.startDate().isAfter(today)
                ? TripStatus.UPCOMING
                : TripStatus.ACTIVE;
//        boolean hasInvitees = req.inviteeUserIds() != null && !req.inviteeUserIds().isEmpty();
//        TripVisibility visibility = hasInvitees ? TripVisibility.FRIENDS : TripVisibility.PRIVATE;
        Trip trip = Trip.builder()
                .owner(owner)
                .title(req.title())
                .description(req.description())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .visibility(TripVisibility.FRIENDS)
                .status(status)
                .place(placeRepository.findById(req.placeId()).orElseThrow(()-> new EntityNotFoundException("Place not found")))
                .build();
        tripRepository.save(trip);
//        if (hasInvitees) {
//            for (Long inviteeId: req.inviteeUserIds()) {
//                if (inviteeId.equals(uid)) continue;
//                User invitee = userRepository.findById(inviteeId)
//                        .orElseThrow(()-> new IllegalArgumentException("User not found"));
//                TripInvitation inv = TripInvitation.builder()
//                        .trip(trip)
//                        .inviter(owner)
//                        .invitee(invitee)
//                        .status(InvitationStatus.PENDING)
//                        .build();
//                tripInvitationRepository.save(inv);
//            }
//        }
        return trip;
    }
    @Transactional
    public List<TripDetailRes> myTrips(Long ownerId) {

        List<Trip> trips = tripRepository.findByOwner_Id(ownerId);
        //        List<TripMediaRes> tripMediaResList = trips.stream().map(trip -> getContents(trip.getId())).toList();
//        List <TripDetailRes tripDetailResList = tripResList.stream().map(res-> new TripDetailRes(res, ))
        return trips.stream().map(trip-> new TripDetailRes(getById(trip.getId()), getContents(trip.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public TripRes getById(Long tripId) {
        // 1) Trip 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("no trip"));

        // 2) 참가자 조회
        List<TripParticipant> participants =
                tripParticipantRepository.findByTrip_Id(tripId);
        logger.info(participants.size() + " participants found");
        logger.info("participants: {}", participants);

        // 3) 이름 / 프사 / 태그 추출
        List<String> names = new ArrayList<>();
        List<String> profileImgs = new ArrayList<>();
        List<String> tags = new ArrayList<>();

        for (TripParticipant tp : participants) {
            User u = tp.getUser();
            // User 필드 이름은 실제 네 코드에 맞춰서 사용 (username / displayName 등)
            names.add(u.getUsername());      // or getName()
            profileImgs.add(u.getAvatarUrl());
            tags.add(u.getTag());
        }

        // 4) TripRes 조립해서 반환
        return TripRes.from(trip, names, profileImgs, tags);
    }
    @Transactional(readOnly = true)
    public TripMediaRes getContents(Long tripId) {
        // 1) 사진 목록 (이미 Repository 메서드가 촬영시각 순으로 반환)
        List<PhotoRes> photos = mediaAssetRepository
                .findByTrip_IdAndContentTypeOrderByTakenAt(tripId, ContentType.PHOTO)
                .stream()
                .map(this::toPhotoRes)
                .toList();

        // 2) 스크랩북 목록
        List<ScrapbookRes> scrapbooks = scrapbookRepository.findByTrip_Id(tripId)
                .stream()
                .map(this::toScrapbookRes)
                .toList();

        // 3) 쇼트릴 + 아이템들
        ShortReel reel = shortReelRepository.findByTrip_Id(tripId).orElse(null);
        ReelRes reelRes = (reel == null) ? null : toReelRes(reel);

        List<ReelItemRes> reelItems = (reel == null)
                ? List.of()
                : shortReelItemRepository.findByReel_Id(reel.getId())
                .stream()
                // position 오름차순(널이면 뒤로)
                .sorted(Comparator.comparing(
                        ShortReelItem::getPosition,
                        Comparator.nullsLast(Integer::compareTo)))
                .map(this::toReelItemRes)
                .toList();

        return new TripMediaRes(photos, scrapbooks, reelItems, reelRes);
    }

    /* ---------- 아래는 DTO 매핑 헬퍼들 ---------- */

    private PhotoRes toPhotoRes(MediaAsset m) {
        return new PhotoRes(toMediaAssetRes(m));
    }

    private ScrapbookRes toScrapbookRes(Scrapbook s) {
        return new ScrapbookRes(
                toMediaAssetRes(s.getCoverMedia()),
                s.getTitle(),
                s.getVisibility(),
                s.getRenderStatus()
        );
    }

    private ReelRes toReelRes(ShortReel r) {
        return new ReelRes(
                toMediaAssetRes(r.getOutputMedia()),
                r.getTitle(),
                r.getRenderStatus()
        );
    }

    private ReelItemRes toReelItemRes(ShortReelItem it) {
        Long tripId = null;
        if (it.getReel() != null && it.getReel().getTrip() != null) {
            tripId = it.getReel().getTrip().getId();
        } else if (it.getMedia() != null && it.getMedia().getTrip() != null) {
            tripId = it.getMedia().getTrip().getId();
        }

        return new ReelItemRes(
                it.getId(),
                tripId,
                it.getPosition(),
                toMediaAssetRes(it.getMedia())
        );
    }

    private MediaAssetRes toMediaAssetRes(MediaAsset m) {
        if (m == null) return null;
        return new MediaAssetRes(
                m.getId(),
                (m.getTrip() != null) ? m.getTrip().getId() : null,
                (m.getMediaKind() != null) ? m.getMediaKind().name() : null,
                (m.getContentType() != null) ? m.getContentType().name() : null,
                m.getComment(),
                m.getUrl(),
                m.getUploader().getId(),
                m.getUploader().getUsername(),                  // 도메인에 존재하는 'uploader' 엔티티 그대로 반환
                m.getWidth(),
                m.getHeight(),
                m.getDurationSec(),
                m.getTakenAt(),
                m.getIsSharedInAlbum(),              // Boolean 필드 네이밍에 따라 getIsSharedInAlbum()/isSharedInAlbum() 중 하나일 수 있음
                m.getExpiration()
        );
    }
}
