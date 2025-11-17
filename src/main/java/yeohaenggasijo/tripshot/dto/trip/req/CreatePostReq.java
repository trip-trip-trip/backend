package yeohaenggasijo.tripshot.dto.trip.req;

import lombok.*;
import yeohaenggasijo.tripshot.domain.common.PostVisibility;
import yeohaenggasijo.tripshot.domain.place.Place;
import yeohaenggasijo.tripshot.domain.post.Post;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostReq {
    // 게시글 정보
    private Long tripId;
    private String visibility; // DB에 저장될 Enum의 String 값
    private String caption;

    // 첨부된 미디어 목록
    private List<MediaAttachmentReq> media;

    public Post toEntity(Trip trip, User author) {
        return Post.builder()
                .trip(trip) // 인자로 받은 Trip 엔티티
                .author(author) // 인자로 받은 User 엔티티
                .place(trip.getPlace())
                // String 값을 Enum으로 변환하여 저장
                .visibility(PostVisibility.valueOf(this.visibility.toUpperCase()))
                .caption(this.caption)
                .build();
    }
}
