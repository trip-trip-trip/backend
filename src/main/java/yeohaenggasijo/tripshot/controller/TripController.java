package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.trip.req.TripCreateReq;
import yeohaenggasijo.tripshot.dto.trip.res.TripRes;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.TripService;
import yeohaenggasijo.tripshot.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final UserService userService;
    private final TripService tripService;
    private final CurrentUserProvider currentUser;

    @PostMapping
    public ResponseEntity<ApiResponse<TripRes>> create(@RequestBody TripCreateReq req) {
        Long uid = currentUser.requireUserId();
        Trip created = tripService.create(uid, req);
        return ResponseEntity
                .created(URI.create("/trips/" + created.getId()))
                .body(ApiResponse.created(TripRes.from(created)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripRes>>> myTrips() {
        Long uid = currentUser.requireUserId();
        List<TripRes> list = tripService.myTrips(uid).stream().map(TripRes::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));

    }


}
