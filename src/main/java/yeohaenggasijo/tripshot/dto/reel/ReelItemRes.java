package yeohaenggasijo.tripshot.dto.reel;

import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;

public record ReelItemRes(
        Long id,
        Long tripId,
        Integer position,
        MediaAssetRes media
) {
}
