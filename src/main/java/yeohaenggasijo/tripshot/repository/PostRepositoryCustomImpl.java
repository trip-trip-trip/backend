package yeohaenggasijo.tripshot.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yeohaenggasijo.tripshot.domain.post.Post;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static yeohaenggasijo.tripshot.domain.post.QPost.post;
import static yeohaenggasijo.tripshot.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findPostListWithFilteringAndCursor(
            String feedType,
            Long targetUserId,
            LocalDateTime cursor,
            int limit){
        return queryFactory
                .selectFrom(post)
                .join(post.author, user).fetchJoin()
                .where(
                        // 커서 기반 페이지네이션 조건
                        lessThanCursor(cursor),
                        // 피드 타입에 따른 메인 필터링 조건
                        postFilterByFeedType(feedType, targetUserId)
                )
                .orderBy(post.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Post> findLocationDataByUserId(Long userId) {
        return queryFactory
                .selectFrom(post)
                // 특정 유저 ID로만 필터링
                .where(post.author.id.eq(userId))
                // 위치 정보는 DTO로 변환될 때 사용
                .fetch();
    }

    // 피드 타입에 따라 필터링 조건 결정
    private BooleanExpression postFilterByFeedType(String feedType, Long targetUserId) {
        if ("profile".equalsIgnoreCase(feedType) && targetUserId != null) {
            // 특정 유저 피드 프로필 조회
            return post.author.id.eq(targetUserId);
        }

        if ("main".equalsIgnoreCase(feedType)) {
            // 메인 피드
            return null; //일단 조건 없이 모두 조회
        }

        //유효하지 않은 feedType이거나 조건 불충족 시
        return null;
    }

    // 커서 조건
    private BooleanExpression lessThanCursor(LocalDateTime cursor) {
        // cursor가 null이면 조건 적용하지 않음
        return cursor != null ? post.createdAt.lt(cursor) : null;
    }


}
