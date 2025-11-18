package yeohaenggasijo.tripshot.dto.user.res;

public record UserProfileRes(
        Long id,
        String username,
        String tag,
        String avatarUrl,
        String bio,
        boolean isMe,              // 내가 나 자신을 보는지
        boolean isFriend,          // 친구인지
        boolean isPendingSent,     // 내가 보낸 PENDING 요청 있는지
        boolean isPendingReceived  // 내가 받은 PENDING 요청 있는지
) {
}
