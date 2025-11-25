package yeohaenggasijo.tripshot.dto.trip.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yeohaenggasijo.tripshot.domain.place.Place;
import yeohaenggasijo.tripshot.domain.post.Like;
import yeohaenggasijo.tripshot.domain.post.Post;
import yeohaenggasijo.tripshot.domain.trip.Trip;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostRes {
    private final Long id;
    private final AuthorRes author;
    private final String caption;

    @JsonProperty("trip_id")
    private final Long tripId;
    private final String visibility;

    @JsonProperty("like_count")
    private final int likeCount;

    @JsonProperty("comment_count")
    private final int commentCount;

    @JsonProperty("is_liked")
    private final boolean isLiked;

    @JsonProperty("is_me")
    private final boolean isMe;

    private List<MediaRes> media;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime updatedAt;

    private final String place;


}
