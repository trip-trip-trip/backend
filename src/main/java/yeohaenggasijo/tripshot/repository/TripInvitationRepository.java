package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.trip.TripInvitation;

public interface TripInvitationRepository extends JpaRepository<TripInvitation, Long> {
}
