package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.post.PostMedia;

import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    @Modifying
    @Query("DELETE FROM PostMedia pm WHERE pm.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);

    List<PostMedia> findByPostIdIn(List<Long> postIds);
}
