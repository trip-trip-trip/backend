package yeohaenggasijo.tripshot.dto.trip.req;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TripUpdateReq {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long placeId;
}