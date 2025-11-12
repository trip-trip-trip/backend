package yeohaenggasijo.tripshot.repository;

import yeohaenggasijo.tripshot.domain.post.Post;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    // 피드 조회 (커서 페이징 + 필터링)
    List<Post> findPostListWithFilteringAndCursor(
            String feedType,
            Long targetUserId,
            LocalDateTime cursor,
            int limit
    );

    // 게시물 위치 조회
    List<Post> findLocationDataByUserId(Long userId);
}
