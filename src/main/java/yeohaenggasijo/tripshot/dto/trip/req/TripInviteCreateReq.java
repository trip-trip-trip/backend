package yeohaenggasijo.tripshot.dto.trip.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * POST /trips/{tripId}/invite
 * 친구 초대 생성 요청 DTO.
 */
public record TripInviteCreateReq(

        @NotEmpty(message = "inviteeUserIds는 비어 있을 수 없습니다.")
        List<@NotNull(message = "inviteeUserId는 null일 수 없습니다.") Long> inviteeUserIds,

        String message // optional
) { }
