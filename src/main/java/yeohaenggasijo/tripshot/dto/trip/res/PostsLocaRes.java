package yeohaenggasijo.tripshot.dto.trip.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import yeohaenggasijo.tripshot.domain.common.AttachmentType;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PostsLocaRes {
    @JsonProperty("post_id")
    private final Long postId;
    private final BigDecimal lat;
    private final BigDecimal lng;

    @JsonProperty("thumbnail_type")
    private final AttachmentType thumbnailType;

    @JsonProperty("thumbnail_url")
    private final String thumbnailUrl;

}
