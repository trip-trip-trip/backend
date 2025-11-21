package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.trip.TripParticipant;

import java.util.List;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    @EntityGraph(attributePaths = "user") // N+1 줄이려고 user까지 같이 페치
    List<TripParticipant> findByTrip_Id(Long tripId);
    boolean existsByTrip_IdAndUser_Id(Long tripId, Long userId);
}
