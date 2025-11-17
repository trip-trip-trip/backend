package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.trip.Trip;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByOwner_Id(Long ownerId);

    // 현재 진행중인 여행 찾기
    @Query("SELECT t FROM Trip t WHERE t.owner.id = :userId " +
            "AND :today BETWEEN t.startDate AND t.endDate " +
            "ORDER BY t.startDate DESC")
    List<Trip> findActiveTrips(@Param("userId") Long userId,
                               @Param("today") LocalDate today);
}
