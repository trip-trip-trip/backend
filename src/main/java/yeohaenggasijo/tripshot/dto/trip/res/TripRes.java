package yeohaenggasijo.tripshot.dto.trip.res;

import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.domain.common.TripVisibility;
import yeohaenggasijo.tripshot.domain.trip.Trip;

import java.time.LocalDate;
import java.util.List;

public record TripRes(
        Long id,
        Long ownerId,
        String title,
        String description,
        TripVisibility visibility,
        TripStatus status,
        LocalDate startDate,
        LocalDate endDate,
        List<String> inviteesProfileImgList,
        List<String> inviteesNameList,
        List<String> inviteesTagList
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
                t.getEndDate(),
                null,
                null,
                null
        );
    }
    public static TripRes fromWithUserInfo(
            Trip t,
            List<String> participantNames,
            List<String> participantAvatarUrls,
            List<String> participantTags
    ) {
        return new TripRes(
                t.getId(),
                t.getOwner() != null ? t.getOwner().getId() : null,
                t.getTitle(),
                t.getDescription(),
                t.getVisibility(),
                t.getStatus(),
                t.getStartDate(),
                t.getEndDate(),
                participantNames,
                participantAvatarUrls,
                participantTags
        );
    }
}
