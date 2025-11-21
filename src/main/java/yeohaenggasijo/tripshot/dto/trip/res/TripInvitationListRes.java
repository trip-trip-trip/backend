package yeohaenggasijo.tripshot.dto.trip.res;

import java.util.List;

/**
 * /trips/{tripId}/invite 응답용 DTO.
 */
public record TripInvitationListRes(
        Long tripId,
        String tripTitle,
        String direction,                  // SENT / RECEIVED
        List<TripInvitationRes> invitations
) { }
