package yeohaenggasijo.tripshot.dto.user.res;

/**
 * 받은 친구 요청 단건 응답 DTO
 * GET /friendships/requests
 */
public record PendingFriendRequestRes(
        Long id,                   // Friendship row id (요청 id)
        Long requesterId,
        String requesterUsername,
        String requesterTag,
        String requesterAvatarUrl
) {
}
