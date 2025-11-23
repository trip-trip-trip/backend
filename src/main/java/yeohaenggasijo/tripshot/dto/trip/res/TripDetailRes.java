package yeohaenggasijo.tripshot.dto.trip.res;

public record TripDetailRes(
        TripRes trip,
        TripMediaRes contents,
        Boolean isOwner
) {
}
