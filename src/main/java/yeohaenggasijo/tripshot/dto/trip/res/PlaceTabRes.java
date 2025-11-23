package yeohaenggasijo.tripshot.dto.trip.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record PlaceTabRes(
        @JsonProperty("place_id")
        Long placeId,

        String name,

        BigDecimal lat,

        BigDecimal lng,

        @JsonProperty("post_count")
        Long postCount
) {
}
