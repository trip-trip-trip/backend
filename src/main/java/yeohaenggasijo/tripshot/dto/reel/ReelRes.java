package yeohaenggasijo.tripshot.dto.reel;

import yeohaenggasijo.tripshot.domain.common.ReelRenderStatus;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;

public record ReelRes(
        MediaAssetRes media,
        String title,
        ReelRenderStatus reelRenderStatus
) {
}
