package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.place.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
