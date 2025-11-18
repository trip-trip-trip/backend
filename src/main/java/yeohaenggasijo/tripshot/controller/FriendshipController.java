package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.user.req.*;
import yeohaenggasijo.tripshot.dto.user.res.*;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final CurrentUserProvider currentUser;

    /**
     * 친구 요청 보내기
     * POST /friendships/requests
     */
    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<FriendRequestStatusRes>> sendFriendRequest(
            @RequestBody FriendRequestCreateReq req
    ) {
        Long userId = currentUser.requireUserId();
        FriendRequestStatusRes res = friendshipService.sendFriendRequest(userId, req);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    /**
     * 받은 친구 요청 목록 조회
     * GET /friendships/requests
     */
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<PendingFriendRequestRes>>> getPendingRequests() {
        Long userId = currentUser.requireUserId();
        List<PendingFriendRequestRes> res = friendshipService.getPendingRequests(userId);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    /**
     * 친구 요청 수락/거절
     * PATCH /friendships/requests/{requestId}
     */
    @PatchMapping("/requests/{requestId}")
    public ResponseEntity<ApiResponse<FriendRequestStatusRes>> handleFriendRequest(
            @PathVariable Long requestId,
            @RequestBody FriendRequestActionReq req
    ) {
        Long userId = currentUser.requireUserId();
        FriendRequestStatusRes res = friendshipService.handleFriendRequest(userId, requestId, req);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }
}
