package yeohaenggasijo.tripshot.dto.user.res;

/**
 * 친구/유저 요약 정보 응답 DTO
 * - 내 친구 목록 조회
 */
public record FriendUserRes(
        Long id,
        String username,
        String tag,
        String avatarUrl,
        String bio
) {
}
