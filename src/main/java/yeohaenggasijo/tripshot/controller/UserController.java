package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.trip.res.PostLocaListRes;
import yeohaenggasijo.tripshot.dto.user.req.UpdateMyProfileReq;
import yeohaenggasijo.tripshot.dto.user.res.MyProfileRes;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.UserService;
import yeohaenggasijo.tripshot.dto.user.res.*;
import yeohaenggasijo.tripshot.service.FriendshipService;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUser;
    private final FriendshipService friendshipService;

    /**
     * 내 프로필 정보 조회
     * GET /users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyProfileRes>> getMyProfile() {
        Long userId = currentUser.requireUserId();
        MyProfileRes profile = userService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    /**
     * 내 프로필 정보 수정
     * PATCH /users/me
     */
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MyProfileRes>> updateMyProfile(
            @RequestBody UpdateMyProfileReq req
    ) {
        Long userId = currentUser.requireUserId();
        MyProfileRes updated = userService.updateMyProfile(userId, req);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    /**
     * 내가 작성한 게시글 목록 조회
     * GET /users/me/posts
     */
    @GetMapping("/me/posts")
    public ResponseEntity<ApiResponse<PostLocaListRes>> getMyPosts() {
        Long userId = currentUser.requireUserId();
        PostLocaListRes posts = userService.getMyPosts(userId);
        return ResponseEntity.ok(ApiResponse.ok(posts));
    }
    // ================= 여기부터 친구/유저 관련 추가 =================

    /**
     * 내 친구 목록 조회
     * GET /users/friendships
     */
    @GetMapping("/friendships")
    public ResponseEntity<ApiResponse<List<FriendUserRes>>> getMyFriends() {
        Long userId = currentUser.requireUserId();
        List<FriendUserRes> friends = friendshipService.getFriends(userId);
        return ResponseEntity.ok(ApiResponse.ok(friends));
    }

    /**
     * 유저 검색 (username/tag 기준)
     * GET /users/friendships/search?keyword=...
     */
    @GetMapping("/friendships/search")
    public ResponseEntity<ApiResponse<List<UserSearchRes>>> searchUsers(
            @RequestParam String keyword
    ) {
        Long userId = currentUser.requireUserId();
        List<UserSearchRes> result = friendshipService.searchUsers(userId, keyword);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * 특정 유저 프로필 조회
     * 특정 유저 프로필 조회
     * GET /users/{userId}/profile
     * - 현재 로그인 유저와의 친구 상태 등은 UserProfileRes 안에 넣으면 됨
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<UserProfileRes>> getUserProfile(
            @PathVariable("userId") Long profileUserId
    ) {
        Long currentUserId = currentUser.requireUserId();
        UserProfileRes profile = friendshipService.getUserProfile(currentUserId, profileUserId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }
}
