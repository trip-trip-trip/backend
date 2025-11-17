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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUser;

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
}
