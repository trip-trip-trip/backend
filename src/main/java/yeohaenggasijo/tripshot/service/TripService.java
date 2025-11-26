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
import yeohaenggasijo.tripshot.dto.trip.req.TripUpdateReq;
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

import static java.lang.System.out;

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

    // 여행 정보 수정
    @Transactional
    public TripRes update(Long uid, Long tripId, TripUpdateReq req) {
        // 여행 존재 확인
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 여행입니다."));

        // 소유자 권한 확인
        if (!trip.getOwner().getId().equals(uid)) {
            throw new IllegalArgumentException("이 여행에 대한 수정 권한이 없습니다.");
        }

        // 여행이 종료되었는지 확인
        LocalDate today = LocalDate.now(clock);
        if (trip.getStatus() == TripStatus.COMPLETED || trip.getEndDate().isBefore(today)) {
            if (req.getStartDate() != null || req.getEndDate() != null || req.getPlaceId() != null) {
                throw new BadRequestException("종료된 여행은 수정할 수 없습니다.");
            }
        }

        // 각 필드 업데이트 (null이 아닌 경우에만)
        if (req.getTitle() != null) {
            trip.setTitle(req.getTitle());
        }

        if (req.getDescription() != null) {
            trip.setDescription(req.getDescription());
        }

        if (req.getStartDate() != null && req.getEndDate() != null) {
            // 날짜 유효성 검사
            if (req.getEndDate().isBefore(req.getStartDate())) {
                throw new BadRequestException("종료일은 시작일보다 이전일 수 없습니다.");
            }
            if (req.getEndDate().isBefore(today)) {
                throw new BadRequestException("종료일은 오늘보다 이전일 수 없습니다.");
            }

            trip.setStartDate(req.getStartDate());
            trip.setEndDate(req.getEndDate());

            // 상태 업데이트
            TripStatus newStatus = req.getStartDate().isAfter(today)
                    ? TripStatus.UPCOMING
                    : TripStatus.ACTIVE;
            trip.setStatus(newStatus);
        }

        if (req.getPlaceId() != null) {
            Place place = placeRepository.findById(req.getPlaceId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 장소입니다."));
            trip.setPlace(place);
        }
        tripRepository.save(trip);

        return TripRes.from(trip);
    }


    /* ---------- 내 여행 목록 ---------- */

    @Transactional
    public List<TripDetailRes> myTrips(Long ownerId) {
        // 기본: 전체 여행
        return myTrips(ownerId, false);
    }

    @Transactional
    public List<TripDetailRes> myTrips(Long ownerId, boolean completedOnly) {
        // 오늘 날짜 (Clock 기반)
        LocalDate today = LocalDate.now(clock);

        List<TripParticipant> tripParticipantList =
                tripParticipantRepository.findByUser_Id(ownerId);

        return tripParticipantList.stream()
                .filter(tp -> {
                    if (!completedOnly) {
                        // 필터링 옵션이 false면 모두 통과
                        return true;
                    }

                    Trip trip = tp.getTrip();
                    LocalDate start = trip.getStartDate();
                    LocalDate end   = trip.getEndDate();

                    // “완료된 여행” 판정 로직
                    // 여기서는: 오늘(today) 이 종료일(end) 이후인 경우를 완료로 본다.
                    //  - end 가 null 이면 완료로 보지 않음
                    //  - today > end 인 경우에만 완료(true)
                    if (end == null) return false;
                    return end.isBefore(today);      // today > end
                    // 만약 "오늘이 종료일인 여행도 완료"로 보고 싶으면:
                    // return !end.isAfter(today);   // end <= today
                })
                .map(tp -> new TripDetailRes(
                        getById(tp.getTrip().getId()),
                        getContents(tp.getTrip().getId()),
                        tp.getRole() == TripParticipantRole.OWNER
                ))
                .toList();
    }

    /* ---------- 단일 여행 정보 ---------- */

    @Transactional(readOnly = true)
    public TripRes getById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("no trip"));

        List<TripParticipant> participants =
                tripParticipantRepository.findByTrip_Id(tripId);
//        logger.info("{} participants found", participants.size());
//        logger.info("participants: {}", participants);

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

    // 예: TripService 또는 TripMediaService 안

    @Transactional(readOnly = true)
    public TripMediaRes getContents(Long tripId) {
        // 로그인 안 한 상태도 고려해서 Optional 사용
        Long currentUserId = currentUserProvider.getUserId().orElse(null);

        // 1) 사진: (isShared || isMine) 인 MediaAsset 만
        List<PhotoRes> photos = mediaAssetRepository
                .findByTrip_IdAndContentTypeOrderByTakenAt(tripId, ContentType.PHOTO)
                .stream()
                .filter(asset -> canViewMedia(asset, currentUserId))  // ★ 필터링 추가
                .map(this::toPhotoRes)
                .toList();

        // 2) 스크랩북: 스크랩북이 가진 표지/대표 미디어 기준으로 필터링
        List<ScrapbookRes> scrapbooks = scrapbookRepository.findByTrip_Id(tripId)
                .stream()
                .filter(sb -> {
                    // 스크랩북이 어떤 미디어를 대표로 들고 있다면 그걸 기준으로 보안 처리
                    MediaAsset cover = sb.getCoverMedia(); // 없으면 null 일 수도 있음
                    boolean mine = currentUserId != null
                            && sb.getCreator() != null
                            && currentUserId.equals(sb.getCreator().getId());
                    boolean shared = (cover != null && canViewMedia(cover, currentUserId));

                    // creator 본인은 항상 볼 수 있고,
                    // coverMedia 가 (isShared || isMine) 이면 볼 수 있게
                    return mine || shared;
                })
                .map(this::toScrapbookRes)
                .toList();

        // 3) 릴: 아예 안 보이는 릴이면 null


        ShortReel reel = shortReelRepository.findByTrip_Id(tripId)
                .filter(r -> {
                    boolean mine = currentUserId != null
                            && r.getCreator() != null
                            && currentUserId.equals(r.getCreator().getId());
                    MediaAsset output = r.getOutputMedia(); // 아직 없을 수도 있음
//                    logger.info("[INFO]output: {}", output);
//                    out.println("output: " + output);
                    boolean shared = (canViewMedia(output, currentUserId));
                    return mine || shared;
                })
                .orElse(null);

        ReelRes reelRes = (reel == null) ? null : toReelRes(reel);

        ShortReel reel_root = shortReelRepository.findByTrip_Id(tripId).orElse(null);
//        logger.info("[INFO] reel_: {}", reel_);

        // 4) 릴 아이템: 해당 아이템이 가진 media 기준으로 필터링
        List<ReelItemRes> reelItems = (reel_root == null)
                ? List.of()
                : shortReelItemRepository.findByReel_Id(reel_root.getId())
                .stream()
                .filter(item -> canViewMedia(item.getMedia(), currentUserId))  // ★ 필터링 추가
                .sorted(Comparator.comparing(
                        ShortReelItem::getPosition,
                        Comparator.nullsLast(Integer::compareTo)))
                .map(this::toReelItemRes)
                .toList();

        return new TripMediaRes(photos, scrapbooks, reelItems, reelRes);
    }

    /**
     * (isShared || isMine) 판별용 공통 헬퍼
     */
    private boolean canViewMedia(MediaAsset asset, Long currentUserId) {
        if (asset == null) return false;

        // isSharedInAlbum == true 인 경우
        boolean isShared = Boolean.TRUE.equals(asset.getIsSharedInAlbum());

        // 업로더 == 현재 유저
        boolean isMine = false;
        if (currentUserId != null && asset.getUploader() != null) {
            isMine = currentUserId.equals(asset.getUploader().getId());
        }

        return isShared || isMine;
    }

    /* ---------- 공유 앨범 공개 여부 토글 ---------- */

    @Transactional
    public void toggleSharedMedias(Long userId, Long tripId, List<Long> toggleIds) {

        // 1) trip 검증
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여행입니다."));

        // 🔥 2) 권한 검증 로직 변경
        TripParticipant participant = tripParticipantRepository
                .findByTrip_IdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new IllegalArgumentException("이 여행에 참여 중인 유저가 아닙니다."));

        // editor, owner만 허용
        if (participant.getRole() != TripParticipantRole.OWNER &&
                participant.getRole() != TripParticipantRole.EDITOR) {
            throw new IllegalArgumentException("이 여행의 공유 앨범을 수정할 권한이 없습니다.");
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
