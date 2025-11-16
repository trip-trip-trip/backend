package yeohaenggasijo.tripshot.dto.media;

import yeohaenggasijo.tripshot.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MediaAssetRes(
        Long mediaAssetId,
        Long tripId,
        String mediaKind,
        String contentType,
        String comment,
        String url,
        Long userId,
        String creatorName,
        Integer width,
        Integer height,
        Integer durationSec,
        LocalDateTime takenAt,
        Boolean isShared,
        LocalDateTime expiration

) {
}
