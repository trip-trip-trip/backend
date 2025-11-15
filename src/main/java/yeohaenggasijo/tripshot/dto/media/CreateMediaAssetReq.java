package yeohaenggasijo.tripshot.dto.media;

public record CreateMediaAssetReq (
        Long tripId,
        String mediaKind,
        String captureType,
        String comment
) {
}
