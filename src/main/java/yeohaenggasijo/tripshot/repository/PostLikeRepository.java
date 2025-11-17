package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.post.Like;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

public interface PostLikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostIdAndUser_Id(Long postId, Long userId);

    // 좋아요 수 일괄 조회: List<Object[]> 반환
    @Query("SELECT l.post.id, COUNT(l) FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<Object[]> findLikeCountsByPostIdsRaw(@Param("postIds") List<Long> postIds);

    // default 메서드로 Map 변환
    default Map<Long, Integer> findLikeCountsByPostIds(List<Long> postIds) {
        List<Object[]> results = findLikeCountsByPostIdsRaw(postIds);
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],                    // postId
                        arr -> ((Number) arr[1]).intValue()      // count
                ));
    }

    @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId AND l.post.id IN :postIds")
    Set<Long> findLikedPostIdsByUserIdAndPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);}
