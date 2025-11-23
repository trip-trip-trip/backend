package yeohaenggasijo.tripshot.dto.trip.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostLocaListRes {
    private final List<PostsLocaRes> posts;

    @JsonProperty("place_tabs")
    private List<PlaceTabRes> placeTabs;
}
