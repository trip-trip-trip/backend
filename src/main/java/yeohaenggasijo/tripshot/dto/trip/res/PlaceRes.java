package yeohaenggasijo.tripshot.dto.trip.res;
import yeohaenggasijo.tripshot.domain.common.PlaceType;
import yeohaenggasijo.tripshot.domain.place.Place;

import java.math.BigDecimal;

public record PlaceRes(
        Long id,
        String name,
        PlaceType type,
        Long parentId,     // 부모 place가 없으면 null
        BigDecimal lat,
        BigDecimal lng
) {
    public static PlaceRes from(Place place) {
        return new PlaceRes(
                place.getId(),
                place.getName(),
                place.getType(),
                place.getParent() != null ? place.getParent().getId() : null,
                place.getLat(),
                place.getLng()
        );
    }
}