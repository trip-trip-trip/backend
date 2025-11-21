package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.common.InvitationStatus;
import yeohaenggasijo.tripshot.domain.trip.TripInvitation;

import java.util.List;

public interface TripInvitationRepository extends JpaRepository<TripInvitation, Long> {
    boolean existsByTrip_IdAndInvitee_IdAndStatus(Long tripId, Long inviteeId, InvitationStatus status);

    List<TripInvitation> findByTrip_Id(Long tripId);

    List<TripInvitation> findByTrip_IdAndInviter_Id(Long tripId, Long inviterId);

    List<TripInvitation> findByTrip_IdAndInvitee_Id(Long tripId, Long inviteeId);
}
