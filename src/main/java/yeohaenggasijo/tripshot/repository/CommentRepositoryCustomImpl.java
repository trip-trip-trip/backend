package yeohaenggasijo.tripshot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yeohaenggasijo.tripshot.domain.post.Comment;

import java.util.List;

import static yeohaenggasijo.tripshot.domain.post.QComment.comment;
import static yeohaenggasijo.tripshot.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findCommentsByPostIdWithAuthor(Long postId) {

        return queryFactory
                .selectFrom(comment)
                // 댓글과 작성자(User) 정보를 단 한 번의 쿼리로 가져와 N+1 방지
                .join(comment.author, user).fetchJoin()
                .where(
                        // 특정 게시글 ID로 필터링
                        comment.post.id.eq(postId)
                )
                .orderBy(comment.createdAt.asc()) // 오래된 댓글 순으로 정렬 (일반적)
                .fetch(); // 쿼리 실행
    }
}
