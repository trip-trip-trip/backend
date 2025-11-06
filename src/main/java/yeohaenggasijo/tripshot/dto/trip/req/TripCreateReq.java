package yeohaenggasijo.tripshot.dto.trip.req;

import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.domain.common.TripVisibility;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public record TripCreateReq(
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        List<Long> inviteeUserIds
) {
}
