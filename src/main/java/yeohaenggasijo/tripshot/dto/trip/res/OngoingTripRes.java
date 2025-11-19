package yeohaenggasijo.tripshot.dto.trip.res;

import java.util.List;

public record OngoingTripRes(
        Boolean isOngoing,
        List<TripRes> trip
) {
    public static OngoingTripRes from(List<TripRes> trip) {
        return new OngoingTripRes(
                true,
                trip
        );
    }

    public static OngoingTripRes empty(){
        return new OngoingTripRes(
          false,
          null
        );
    }
}
