package yeohaenggasijo.tripshot.dto.user.req;

/**
 * 친구 요청 처리(수락/거절) 요청 DTO
 * PATCH /friendships/requests/{requestId}
 *
 * action: "ACCEPT" 또는 "REJECT"
 */
public record FriendRequestActionReq(
        String action
) {
}
