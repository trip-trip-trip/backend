package yeohaenggasijo.tripshot.dto.trip.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostLikesRes {
    private final Long postId;
    private final int likeCount;
}
