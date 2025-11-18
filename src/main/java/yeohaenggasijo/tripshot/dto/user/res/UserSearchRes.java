package yeohaenggasijo.tripshot.dto.user.res;

/**
 * 유저 검색 결과 응답 DTO
 * GET /users/friendships/search
 */
public record UserSearchRes(
        Long id,
        String username,
        String tag,
        String avatarUrl,
        String bio,
        boolean isFriend,          // 이미 친구인지
        boolean isPendingSent,     // 내가 보낸 PENDING 요청 존재 여부
        boolean isPendingReceived  // 내가 받은 PENDING 요청 존재 여부
) {
}
