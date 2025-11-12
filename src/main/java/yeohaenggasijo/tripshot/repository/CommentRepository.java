package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeohaenggasijo.tripshot.domain.post.Comment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    // 댓글 수 일괄 조회: List<Object[]> 반환
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> findCommentCountsByPostIdsRaw(@Param("postIds") List<Long> postIds);

    // default 메서드로 Map 변환
    default Map<Long, Integer> findCommentCountsByPostIds(List<Long> postIds) {
        List<Object[]> results = findCommentCountsByPostIdsRaw(postIds);
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],                    // postId
                        arr -> ((Number) arr[1]).intValue()      // count
                ));
    }

    int countByPostId(Long postId);

    Optional<Comment> findByPostIdAndId(Long postId, Long commentId);

    List<Comment> findByPostId(Long postId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdWithCommenter(Long postId);
}
