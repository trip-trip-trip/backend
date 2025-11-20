package yeohaenggasijo.tripshot.dto.trip.req;

import jakarta.validation.constraints.NotNull;

/**
 * PATCH /trips/share_album
 * 여행의 공유 앨범 공개 여부를 토글하는 요청 DTO.
 */
public record TripShareAlbumReq(
        @NotNull(message = "tripId는 필수 값입니다.")
        Long tripId,

        @NotNull(message = "isShared 값은 필수입니다.")
        Boolean isShared
) { }
