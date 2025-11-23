package yeohaenggasijo.tripshot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.trip.req.CreateCommentReq;
import yeohaenggasijo.tripshot.dto.trip.req.CreatePostReq;
import yeohaenggasijo.tripshot.dto.trip.req.UpdatePostReq;
import yeohaenggasijo.tripshot.dto.trip.res.*;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.MainPageService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final MainPageService mainPageService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ApiResponse<PostRes> createPost(
            // TODO: 인증 정보에서 실제 유저 ID를 가져오는 코드로 교체해야 함
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId,
            @RequestBody @Valid CreatePostReq request
    ) {
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            PostRes response = mainPageService.createPost(currentUserId, request);

            // ApiResponse.created(T data) 사용: HTTP 201 Created와 함께 'result'에 데이터 반환
            return ApiResponse.created(response);

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 작성 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/{post_id}")
    public ApiResponse<PostRes> updatePost(
            @PathVariable("post_id") Long postId,
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId,
            @RequestBody @Valid UpdatePostReq request
    ) {
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            PostRes response = mainPageService.updatePost(postId, currentUserId, request);

            // ApiResponse.ok(T data) 사용: HTTP 200 OK와 함께 'result'에 데이터 반환
            return ApiResponse.ok(response);

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (IllegalStateException e) {
            // 권한 오류(IllegalStateException)는 403 Forbidden 처리
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 작성 중 알 수 없는 오류가 발생했습니다.");
        }

    }

    @DeleteMapping("/{post_id}")
    public ApiResponse<Void> deletePost(
            @PathVariable("post_id") Long postId,
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId
    ) {
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            mainPageService.deletePost(postId, currentUserId);

            // 삭제 성공: HTTP 200 OK
            return ApiResponse.ok();

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (IllegalStateException e) {
            // 권한 오류(IllegalStateException)는 403 Forbidden 처리
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 삭제 중 알 수 없는 오류가 발생했습니다.");
        }

    }

    // 토글로 좋아요/취소 한번에 관리
    @PostMapping("/{post_id}/like")
    public ApiResponse<Void> togglePostLike(
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId,
            @PathVariable("post_id") Long postId
    ) {
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            // Service의 toggleLike 호출: true면 생성, false면 삭제
            boolean isLiked = mainPageService.toggleLike(postId, currentUserId);

            if (isLiked) {
                // 좋아요 생성 (201 Created)
                return ApiResponse.created(null);
            } else {
                // 좋아요 취소 (204 No Content)
                return ApiResponse.noContent();
            }
        } catch (NoSuchElementException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "좋아요 대상 게시글이나 유저를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "좋아요 처리 중 오류가 발생했습니다.");
        }
    }

    // 댓글 작성
    @PostMapping("/{post_id}/comment")
    public ApiResponse<CommentRes> createComment(
            @PathVariable("post_id") Long postId,
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId,
            @RequestBody @Valid CreateCommentReq request
    ) {
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            CommentRes response = mainPageService.createComment(postId, currentUserId, request);

            // ApiResponse.ok(T data) 사용: HTTP 201 created와 함께 'result'에 데이터 반환
            return ApiResponse.created(response);

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (IllegalStateException e) {
            // 권한 오류(IllegalStateException)는 403 Forbidden 처리
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "댓글 작성 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/{post_id}/comment/{comment_id}")
    public ApiResponse<Void> deleteComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId,
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId
    ) {
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            mainPageService.deleteComment(postId, commentId, currentUserId);

            // 삭제 성공: HTTP 200 OK
            return ApiResponse.ok();

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (IllegalStateException e) {
            // 권한 오류(IllegalStateException)는 403 Forbidden 처리
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "댓글 삭제 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/{post_id}/comments")
    public ApiResponse<CommentListRes> getCommentList(
            @PathVariable("post_id") Long postId,
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId
    ){
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            CommentListRes response = mainPageService.getCommentList(postId,  currentUserId);

            // 댓글 리스트 조회 성공
            return ApiResponse.ok(response);

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (IllegalStateException e) {
            // 권한 오류(IllegalStateException)는 403 Forbidden 처리
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 댓글 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/locations")
    public ApiResponse<PostLocaListRes> getPostLoca(
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId
    ){
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            PostLocaListRes response = mainPageService.getPostLoca(currentUserId);

            // 위치 불러오기 성공
            return ApiResponse.ok(response);

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (IllegalStateException e) {
            // 권한 오류(IllegalStateException)는 403 Forbidden 처리
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 위치 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/{post_id}")
    public ApiResponse<PostRes> getPost(
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId,
            @PathVariable("post_id") Long postId
    ){
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        try {
            PostRes response = mainPageService.getPost(postId,  currentUserId);
            return ApiResponse.ok(response);

        } catch (NoSuchElementException e) {
            // 필수 연관 엔티티(유저/여행 정보 등)를 찾지 못한 경우: 404 Not Found에 준하는 에러
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (IllegalStateException e) {
            // 권한 오류(IllegalStateException)는 403 Forbidden 처리
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 조회 중 알 수 없는 오류가 발생했습니다.");
        }

    }

    @GetMapping
    public ApiResponse<AllPostListRes> getPostFeed(
            @RequestParam(value = "feed_type", defaultValue = "main") String feedType,
            @RequestParam(value = "user_id", required = false) Long targetUserId,
            @RequestParam(value = "cursor", required = false) String cursorString, // 커서를 String으로 받음
            @RequestParam(value = "limit", defaultValue = "10") int limit, // 페이지 크기
            @RequestHeader(value = "X-USER-ID", defaultValue = "1") Long currentUserId
    ) {
        Optional<Long> loggedInUser = currentUserProvider.getUserId();
        if (loggedInUser.isPresent()) {
            currentUserId = loggedInUser.get();
        }
        LocalDateTime cursor = null;
        try {
            // 1. String으로 받은 커서 값을 LocalDateTime으로 안전하게 변환
            if (cursorString != null && !cursorString.trim().isEmpty()) {
                // ISO 8601 형식 (yyyy-MM-dd'T'HH:mm:ss.SSS)을 가정하고 파싱
                cursor = LocalDateTime.parse(cursorString);
            }

            // 2. Service 호출
            AllPostListRes response = mainPageService.getPostFeed(feedType, targetUserId, cursor, limit, currentUserId);

            // 3. 성공 응답 반환 (HTTP 200 OK)
            return ApiResponse.ok(response);

        } catch (DateTimeParseException e) {
            // 잘못된 커서 형식 오류: 400 Bad Request
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "잘못된 커서(cursor) 형식입니다. 유효한 시간 문자열을 제공해야 합니다.");
        } catch (NoSuchElementException e) {
            // 찾을 수 없는 리소스 관련 오류 (예: 존재하지 않는 targetUserId): 404 Not Found
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (Exception e) {
            // 그 외 서버 오류: 500 Internal Server Error
            e.printStackTrace(); // 서버 로그에 스택 트레이스 기록
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 피드 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }
}
