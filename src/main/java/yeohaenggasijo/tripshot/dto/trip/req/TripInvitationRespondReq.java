package yeohaenggasijo.tripshot.dto.trip.req;

import jakarta.validation.constraints.NotBlank;

/**
 * PATCH /invitations/{invitationId}
 * 초대 수락/거절 요청 DTO.
 */
public record TripInvitationRespondReq(
        @NotBlank(message = "decision 값은 필수입니다. (ACCEPT 또는 REJECT)")
        String decision
) { }
