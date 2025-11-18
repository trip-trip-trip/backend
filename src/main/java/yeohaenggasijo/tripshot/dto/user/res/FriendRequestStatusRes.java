package yeohaenggasijo.tripshot.dto.user.res;

/**
 * 친구 요청 생성/처리 후 상태 응답 DTO
 * - POST /friendships/requests
 * - PATCH /friendships/requests/{requestId}
 */
public record FriendRequestStatusRes(
        Long id,       // friendship id
        String status  // PENDING / ACCEPTED / BLOCKED (FriendshipStatus.name())
) {
}
