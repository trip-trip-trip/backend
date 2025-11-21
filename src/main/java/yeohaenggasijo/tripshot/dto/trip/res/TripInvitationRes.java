package yeohaenggasijo.tripshot.dto.trip.res;

import java.time.LocalDateTime;

public record TripInvitationRes(
        Long invitationId,
        Long tripId,
        String tripTitle,

        Long inviterUserId,
        String inviterUsername,
        String inviterTag,
        String inviterAvatarUrl,

        Long inviteeUserId,
        String inviteeUsername,
        String inviteeTag,
        String inviteeAvatarUrl,

        String status,              // InvitationStatus.name()
        LocalDateTime createdAt,
        LocalDateTime respondedAt
) { }
