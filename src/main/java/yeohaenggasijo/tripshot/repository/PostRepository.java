package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.post.Post;
import yeohaenggasijo.tripshot.dto.trip.res.PlaceTabRes;
import yeohaenggasijo.tripshot.dto.trip.res.PostsLocaRes;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    @Query("""
            SELECT new yeohaenggasijo.tripshot.dto.trip.res.PostsLocaRes(
                p.id, 
                p.place.lat, 
                p.place.lng, 
                pm.objectType,
                CASE pm.objectType 
                    WHEN yeohaenggasijo.tripshot.domain.common.AttachmentType.MEDIA THEN ma.url
                    WHEN yeohaenggasijo.tripshot.domain.common.AttachmentType.SCRAPBOOK THEN ma.url
                    WHEN yeohaenggasijo.tripshot.domain.common.AttachmentType.SHORT_REEL THEN ma.thumbnailUrl
                    ELSE null
                END
            )
            FROM Post p
            LEFT JOIN PostMedia pm ON pm.post.id = p.id AND pm.position = 1
            LEFT JOIN MediaAsset ma ON pm.objectId = ma.id
            WHERE p.author.id = :userId
            """) // 특정 유저 ID로 필터링
    List<PostsLocaRes> findAllPostLocaByAuthorId(@Param("userId") Long userId);

    Integer countPostByAuthor_Id(Long authorId);

    // 추가: 고유한 장소 목록 (탭용)
    @Query("""
            SELECT new yeohaenggasijo.tripshot.dto.trip.res.PlaceTabRes(
                pl.id,
                pl.name,
                pl.lat,
                pl.lng,
                COUNT(p.id)
            )
            FROM Post p
            JOIN p.place pl
            WHERE p.author.id = :userId
            GROUP BY pl.id, pl.name, pl.lat, pl.lng
            ORDER BY COUNT(p.id) DESC
            """)
    List<PlaceTabRes> findDistinctPlacesByAuthorId(@Param("userId") Long userId);

}
