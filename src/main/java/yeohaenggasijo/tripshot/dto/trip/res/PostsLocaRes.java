package yeohaenggasijo.tripshot.dto.trip.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PostsLocaRes {
    @JsonProperty("post_id")
    private final Long postId;
    private final BigDecimal lat;
    private final BigDecimal lng;

}
