package yeohaenggasijo.tripshot.dto.scrapbook;

import yeohaenggasijo.tripshot.domain.common.ScrapbookRenderStatus;
import yeohaenggasijo.tripshot.domain.common.ScrapbookVisibility;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;

public record ScrapbookRes (
        MediaAssetRes media,
        String title,

        ScrapbookVisibility visibility,
        ScrapbookRenderStatus renderStatus
) {
}
