package yeohaenggasijo.tripshot.dto.user.res;

/**
 * 보낸 친구 요청 단건 응답 DTO
 * GET /friendships/requests
 */
public record PendingSentFriendRequestRes(
        Long id,                   // Friendship row id (요청 id)
        Long receiverId,
        String receiverUsername,
        String receiverTag,
        String receiverAvatarUrl
) {
}
