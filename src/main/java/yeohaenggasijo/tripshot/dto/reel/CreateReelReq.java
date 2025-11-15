package yeohaenggasijo.tripshot.dto.reel;

import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;

public record CreateReelReq(
        CreateMediaAssetReq media,
        String title
) {
}
