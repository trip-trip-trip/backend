package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.reel.ReelStatusRes;
import yeohaenggasijo.tripshot.dto.trip.res.*;
import yeohaenggasijo.tripshot.dto.trip.req.*;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.ShortReelService;
import yeohaenggasijo.tripshot.service.TripService;
import yeohaenggasijo.tripshot.service.TripInvitationService;
import yeohaenggasijo.tripshot.service.UserService;


import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final UserService userService;
    private final TripService tripService;
    private final CurrentUserProvider currentUser;
    private final ShortReelService shortReelService;
    private final TripInvitationService tripInvitationService;
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    @PostMapping
    public ResponseEntity<ApiResponse<TripRes>> create(@RequestBody TripCreateReq req) {
        Long uid = currentUser.requireUserId();
        Trip created = tripService.create(uid, req);
        return ResponseEntity
                .created(URI.create("/trips/" + created.getId()))
                .body(ApiResponse.created(TripRes.from(created)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripDetailRes>>> myTrips(
            @RequestParam(name = "completedOnly", required = false, defaultValue = "false")
            boolean completedOnly
    ) {
        Long uid = currentUser.requireUserId();
        List<TripDetailRes> list = tripService.myTrips(uid, completedOnly);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripDetailRes>> detail(@PathVariable Long id) {
        Long uid = currentUser.requireUserId();
        TripRes data = tripService.getById(id);
        TripMediaRes mediaData = tripService.getContents(id);
//        logger.info("[INFO] mediaData: {}", data);
        Boolean isOwner = Objects.equals(data.ownerId(), uid);
        return ResponseEntity.ok(ApiResponse.ok(new TripDetailRes(data, mediaData, isOwner)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TripRes>> update(
            @PathVariable Long id,
            @RequestBody TripUpdateReq req

    ) {
        Long uid = currentUser.requireUserId();
        TripRes updated = tripService.update(uid, id, req);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @GetMapping("/{id}/reel")
    public ResponseEntity<ApiResponse<ReelStatusRes>> reel(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(ApiResponse.ok(shortReelService.getOrQueueWhenEnded(id)));
    }



    @GetMapping("/isActiveTrips")
    public ResponseEntity<ApiResponse<OngoingTripRes>> isTraveling(){
        return ResponseEntity.ok(ApiResponse.ok(tripService.isActiveTrip()));
    }

    @GetMapping("/places")
    public ResponseEntity<ApiResponse<List<PlaceRes>>> getPlaces() {
        List<PlaceRes> places = tripService.getAllPlaces();
        return ResponseEntity.ok(
                new ApiResponse<>(true, 200, "OK", places)
        );
    }

    @PatchMapping("/{tripId}/shared_media/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleSharedMedia(
            @PathVariable Long tripId,
            @RequestBody TripToggleShareMediaReq req
    ) {
        Long uid = currentUser.requireUserId();
        tripService.toggleSharedMedias(uid, tripId, req.sharedMediaIds());

        return ResponseEntity.ok(
                ApiResponse.of(true, 200, "공유 상태가 변경되었습니다.", null)
        );
    }

    // 친구 초대 보내기
    @PostMapping("/{tripId}/invite")
    public ResponseEntity<ApiResponse<TripInvitationListRes>> inviteFriends(
            @PathVariable Long tripId,
            @RequestBody TripInviteCreateReq req
    ) {
        Long uid = currentUser.requireUserId();
        TripInvitationListRes result = tripInvitationService.sendInvitations(uid, tripId, req);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // 초대 현황 조회
    @GetMapping("/{tripId}/invite")
    public ResponseEntity<ApiResponse<TripInvitationListRes>> getInvitations(
            @PathVariable Long tripId,
            @RequestParam(required = false) String direction
    ) {
        Long uid = currentUser.requireUserId();
        TripInvitationListRes result = tripInvitationService.getTripInvitations(uid, tripId, direction);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }






}
