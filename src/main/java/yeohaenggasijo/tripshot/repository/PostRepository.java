package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.post.Post;
import yeohaenggasijo.tripshot.dto.trip.res.PostsLocaRes;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    @Query("SELECT new yeohaenggasijo.tripshot.dto.trip.res.PostsLocaRes(p.id, p.place.lat, p.place.lng) " +
            "FROM Post p " +
            "WHERE p.author.id = :userId") // 특정 유저 ID로 필터링
    List<PostsLocaRes> findAllPostLocaByAuthorId(@Param("userId") Long userId);
}
