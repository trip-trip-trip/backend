package yeohaenggasijo.tripshot.dto.trip.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MediaRes {
    private final Long id;
    private final String url;
    private final String thumbnailUrl;
    private final String mediaKind;
    private final int position;
}
