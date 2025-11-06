package yeohaenggasijo.tripshot.dto.trip.res;

import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.domain.common.TripVisibility;
import yeohaenggasijo.tripshot.domain.trip.Trip;

import java.time.LocalDate;

public record TripRes(
        Long id,
        Long ownerId,
        String title,
        String description,
        TripVisibility visibility,
        TripStatus status,
        LocalDate startDate,
        LocalDate endDate
) {
    public static TripRes from(Trip t){
        return new TripRes(
                t.getId(),
                t.getOwner() != null ? t.getOwner().getId() : null,
                t.getTitle(),
                t.getDescription(),
                t.getVisibility(),
                t.getStatus(),
                t.getStartDate(),
                t.getEndDate()
        );
    }
}
