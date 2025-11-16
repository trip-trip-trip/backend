package yeohaenggasijo.tripshot.dto.reel;

import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;

public record CreateReelItemReq(
        CreateMediaAssetReq media,
        Long tripId
) {
}
