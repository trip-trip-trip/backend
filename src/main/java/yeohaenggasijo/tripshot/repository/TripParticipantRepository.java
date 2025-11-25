package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.trip.TripParticipant;
import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.domain.user.User;

import java.time.LocalDate;
import java.util.List;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    @EntityGraph(attributePaths = "user") // N+1 줄이려고 user까지 같이 페치
    List<TripParticipant> findByTrip_Id(Long tripId);
    boolean existsByTrip_IdAndUser_Id(Long tripId, Long userId);

    Integer countByUser(User target);

    List<TripParticipant> findByUser_Id(Long ownerId);

    // Check if the user is already partiscipating in any ACTIVE trip
    boolean existsByUser_IdAndTrip_Status(Long userId, TripStatus status);

    @Query("""
    select 
        case when count(tp) > 0 then true else false end
    from TripParticipant tp
    where tp.user.id = :userId
      and :today between tp.trip.startDate and tp.trip.endDate
""")
    boolean existsActiveTripForUser(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );
}
