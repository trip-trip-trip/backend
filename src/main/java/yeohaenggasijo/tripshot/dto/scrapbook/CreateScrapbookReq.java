package yeohaenggasijo.tripshot.dto.scrapbook;

import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;

public record CreateScrapbookReq(
        CreateMediaAssetReq media,
        Long tripId,
        String title
) {
}
