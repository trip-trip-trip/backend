package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.reel.ShortReel;

import java.util.Optional;

public interface ShortReelRepository extends JpaRepository<ShortReel, Long> {
    Optional<ShortReel> findByTrip_Id(Long tripId);
}
