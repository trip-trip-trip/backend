package yeohaenggasijo.tripshot.dto.trip.res;

/**
 * 공유 앨범 공개 여부 변경 후 응답 DTO.
 */
public record TripShareAlbumRes(
        Long tripId,
        Long albumId,
        Boolean isShared
) { }
