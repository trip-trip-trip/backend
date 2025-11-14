package yeohaenggasijo.tripshot.dto.trip.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CommentListRes {
    private final List<CommentRes> comments;
}
