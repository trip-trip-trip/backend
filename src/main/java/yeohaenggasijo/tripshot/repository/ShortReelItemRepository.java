package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.reel.ShortReelItem;

import java.util.List;
import java.util.Optional;

public interface ShortReelItemRepository extends JpaRepository<ShortReelItem, Long> {
    List<ShortReelItem> findByReel_Id(Long reelId);
    List<ShortReelItem> findByReel_IdOrderByPositionAsc(Long reelId);

    @Query("""
      select coalesce(max(i.position), -1) + 1
      from ShortReelItem i
      where i.reel.id = :reelId
    """)
    int nextPosition(@Param("reelId") Long reelId);

    Optional<ShortReelItem> findByReel_IdAndMedia_Id(Long reelId, Long mediaId); // 멱등 보호용
}
