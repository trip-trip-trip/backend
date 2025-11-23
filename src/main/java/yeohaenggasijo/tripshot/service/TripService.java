package yeohaenggasijo.tripshot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.album.Album;
import yeohaenggasijo.tripshot.domain.common.ContentType;
import yeohaenggasijo.tripshot.domain.common.TripParticipantRole;
import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.domain.common.TripVisibility;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.place.Place;
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
import yeohaenggasijo.tripshot.dto.trip.req.TripShareAlbumReq;
import yeohaenggasijo.tripshot.dto.trip.res.*;
import yeohaenggasijo.tripshot.exception.BadRequestException;
import yeohaenggasijo.tripshot.repository.*;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;


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
    private final AlbumRepository albumRepository;

    private static final Logger logger = LoggerFactory.getLogger(TripService.class);
    private final CurrentUserProvider currentUserProvider;

    /* ---------- 여행 생성 ---------- */

    @Transactional
    public Trip create(Long uid, TripCreateReq req) {
        User owner = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now(clock);
        if (req.endDate().isBefore(today)) {
            throw new BadRequestException("End date already passed");
        }

        TripStatus status = req.startDate().isAfter(today)
                ? TripStatus.UPCOMING
                : TripStatus.ACTIVE;

        Trip trip = Trip.builder()
                .owner(owner)
                .title(req.title())
                .description(req.description())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .visibility(TripVisibility.FRIENDS)
                .status(status)
                .place(placeRepository.findById(req.placeId())
                        .orElseThrow(() -> new EntityNotFoundException("Place not found")))
                .build();

        tripRepository.save(trip);

        tripParticipantRepository.save(
                TripParticipant.builder()
                        .trip(trip)
                        .user(owner)
                        .role(TripParticipantRole.OWNER)
                        .build()
        );
        return trip;
    }

    /* ---------- 내 여행 목록 ---------- */

    @Transactional
    public List<TripDetailRes> myTrips(Long ownerId) {
//        List<Trip> trips = tripRepository.findByOwner_Id(ownerId);
        List<TripParticipant> tripParticipantList = tripParticipantRepository.findByUser_Id(ownerId);
        return tripParticipantList.stream()
                .map(tripParticipant -> new TripDetailRes(
                                getById(tripParticipant.getTrip().getId()),
                                getContents(tripParticipant.getTrip().getId()),
                                tripParticipant.getRole() == TripParticipantRole.OWNER
                        )
                )
                .toList();
    }

    /* ---------- 단일 여행 정보 ---------- */

    @Transactional(readOnly = true)
    public TripRes getById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("no trip"));

        List<TripParticipant> participants =
                tripParticipantRepository.findByTrip_Id(tripId);
        logger.info("{} participants found", participants.size());
        logger.info("participants: {}", participants);

        List<String> names = new ArrayList<>();
        List<String> profileImgs = new ArrayList<>();
        List<String> tags = new ArrayList<>();

        for (TripParticipant tp : participants) {
            User u = tp.getUser();
            names.add(u.getUsername());
            profileImgs.add(u.getAvatarUrl());
            tags.add(u.getTag());
        }

        // 4) TripRes 조립해서 반환
        return TripRes.fromWithUserInfo(trip, names, profileImgs, tags);
    }

    /* ---------- 여행에 속한 미디어(사진/스크랩북/릴) ---------- */

    @Transactional(readOnly = true)
    public TripMediaRes getContents(Long tripId) {
        List<PhotoRes> photos = mediaAssetRepository
                .findByTrip_IdAndContentTypeOrderByTakenAt(tripId, ContentType.PHOTO)
                .stream()
                .map(this::toPhotoRes)
                .toList();

        List<ScrapbookRes> scrapbooks = scrapbookRepository.findByTrip_Id(tripId)
                .stream()
                .map(this::toScrapbookRes)
                .toList();

        ShortReel reel = shortReelRepository.findByTrip_Id(tripId).orElse(null);
        ReelRes reelRes = (reel == null) ? null : toReelRes(reel);

        List<ReelItemRes> reelItems = (reel == null)
                ? List.of()
                : shortReelItemRepository.findByReel_Id(reel.getId())
                .stream()
                .sorted(Comparator.comparing(
                        ShortReelItem::getPosition,
                        Comparator.nullsLast(Integer::compareTo)))
                .map(this::toReelItemRes)
                .toList();

        return new TripMediaRes(photos, scrapbooks, reelItems, reelRes);
    }

    /* ---------- 공유 앨범 공개 여부 토글 ---------- */

    @Transactional
    public void toggleSharedMedias(Long userId, Long tripId, List<Long> toggleIds) {

        // 1) trip 검증
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여행입니다."));

        // 2) owner 권한 검증
        if (!trip.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("이 여행에 대한 수정 권한이 없습니다.");
        }

        // 3) trip의 모든 MediaAsset 조회
        List<MediaAsset> assets = mediaAssetRepository.findByTrip_Id(tripId);

        // 4) toggleIds 중 trip에 속하지 않는 media가 있는지 검증
        for (Long id : toggleIds) {
            boolean exists = assets.stream().anyMatch(a -> a.getId().equals(id));
            if (!exists) {
                throw new IllegalArgumentException("요청한 미디어가 해당 여행에 포함되지 않습니다. id=" + id);
            }
        }

        // 5) 토글 처리
        for (MediaAsset asset : assets) {
            if (toggleIds.contains(asset.getId())) {
                // ON → OFF, OFF → ON
                boolean current = Boolean.TRUE.equals(asset.getIsSharedInAlbum());
                asset.setIsSharedInAlbum(!current);
            }
        }
    }


    @Transactional(readOnly = true)
    public OngoingTripRes isActiveTrip() {
        Long loggedInUserId = currentUserProvider.requireUserId();
        List<Trip> ongoingTrip = tripRepository.findActiveTrips(loggedInUserId, LocalDate.now());
        if (ongoingTrip.isEmpty()) {
            return OngoingTripRes.empty();
        }

        List<TripRes> dataList = new ArrayList<>();
        for (Trip trip : ongoingTrip) {
            List<TripParticipant> tripParticipantList = tripParticipantRepository.findByTrip_Id(trip.getId());
            List<User> userList = tripParticipantList.stream().map(TripParticipant::getUser).toList();
            List<String> userNameList = userList.stream().map(User::getUsername).toList();
            List<String> profileImgList = userList.stream().map(User::getAvatarUrl).toList();
            List<String> tagList = userList.stream().map(User::getTag).toList();
            TripRes tripRes = TripRes.fromWithUserInfo(trip, userNameList, profileImgList, tagList);
            dataList.add(tripRes);
        }

        return OngoingTripRes.from(dataList);


    }

    @Transactional(readOnly = true)
    public List<PlaceRes> getAllPlaces() {
        List<Place> places = placeRepository.findAll(); // 필요하면 Sort 추가 가능

        return places.stream()
                .map(PlaceRes::from)
                .toList();
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
                m.getUploader().getUsername(),
                m.getWidth(),
                m.getHeight(),
                m.getDurationSec(),
                m.getTakenAt(),
                m.getIsSharedInAlbum(),
                m.getExpiration()
        );
    }
}
