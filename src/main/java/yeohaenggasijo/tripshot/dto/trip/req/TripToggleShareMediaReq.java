package yeohaenggasijo.tripshot.dto.trip.req;

import java.util.List;

public record TripToggleShareMediaReq(
        List<Long> sharedMediaIds
) {}
