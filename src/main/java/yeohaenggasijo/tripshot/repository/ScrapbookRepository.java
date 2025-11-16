package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.scrapbook.Scrapbook;

import java.util.List;

public interface ScrapbookRepository extends JpaRepository<Scrapbook, Long> {
    List<Scrapbook> findByTrip_Id(Long tripId);
}
