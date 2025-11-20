package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.reel.ReelStatusRes;
import yeohaenggasijo.tripshot.dto.trip.req.TripCreateReq;
import yeohaenggasijo.tripshot.dto.trip.req.TripShareAlbumReq;
import yeohaenggasijo.tripshot.dto.trip.req.TripToggleShareMediaReq;
import yeohaenggasijo.tripshot.dto.trip.res.*;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.ShortReelService;
import yeohaenggasijo.tripshot.service.TripService;
import yeohaenggasijo.tripshot.service.UserService;


import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final UserService userService;
    private final TripService tripService;
    private final CurrentUserProvider currentUser;
    private final ShortReelService shortReelService;

    @PostMapping
    public ResponseEntity<ApiResponse<TripRes>> create(@RequestBody TripCreateReq req) {
        Long uid = currentUser.requireUserId();
        Trip created = tripService.create(uid, req);
        return ResponseEntity
                .created(URI.create("/trips/" + created.getId()))
                .body(ApiResponse.created(TripRes.from(created)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripDetailRes>>> myTrips() {
        Long uid = currentUser.requireUserId();
        List<TripDetailRes> list = tripService.myTrips(uid);
        return ResponseEntity.ok(ApiResponse.ok(list));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripDetailRes>> detail(@PathVariable Long id) {
        Long uid = currentUser.requireUserId();
        TripRes data = tripService.getById(id);
        TripMediaRes mediaData = tripService.getContents(id);
        return ResponseEntity.ok(ApiResponse.ok(new TripDetailRes(data, mediaData)));
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




}
