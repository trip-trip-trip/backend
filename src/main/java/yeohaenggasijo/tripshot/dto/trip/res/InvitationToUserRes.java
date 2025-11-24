package yeohaenggasijo.tripshot.dto.trip.res;

import yeohaenggasijo.tripshot.domain.common.InvitationStatus;
import yeohaenggasijo.tripshot.domain.trip.TripInvitation;

import java.time.LocalDateTime;

public record InvitationToUserRes(
        Long userId,
        LocalDateTime createdAt,
        String inviterProfileImg,
        String inviterName,
        String tripName,
        Long tripId,
        InvitationStatus status

) {

}
