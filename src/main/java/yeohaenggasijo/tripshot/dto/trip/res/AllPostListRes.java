package yeohaenggasijo.tripshot.dto.trip.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AllPostListRes {
    // 게시글 목록
    private final List<PostRes> posts;
    // 다음 페이지 요청할때 사용할 커서 값
    private final LocalDateTime nextCursor;
    // 다음 페이지 존재 여부
    private final boolean hashNext;
}
