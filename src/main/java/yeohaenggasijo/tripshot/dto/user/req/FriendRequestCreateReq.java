package yeohaenggasijo.tripshot.dto.user.req;

/**
 * 친구 요청 보내기 요청 DTO
 * POST /friendships/requests
 */
public record FriendRequestCreateReq(
        Long targetUserId
) {
}
