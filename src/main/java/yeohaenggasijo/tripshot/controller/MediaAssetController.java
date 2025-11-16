package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yeohaenggasijo.tripshot.domain.common.ContentType;
import yeohaenggasijo.tripshot.domain.common.ReelTransition;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.dto.reel.CreateReelItemReq;
import yeohaenggasijo.tripshot.dto.reel.ReelItemRes;
import yeohaenggasijo.tripshot.dto.scrapbook.CreateScrapbookReq;
import yeohaenggasijo.tripshot.dto.scrapbook.ScrapbookRes;
import yeohaenggasijo.tripshot.service.MediaAssetService;
import yeohaenggasijo.tripshot.service.ScrapbookService;
import yeohaenggasijo.tripshot.service.ShortReelService;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaAssetController {

    private final MediaAssetService mediaAssetService;
    private final ShortReelService shortReelService;
    private final ScrapbookService scrapbookService;

    /**
     * 사진 업로드 (이미지): multipart/form-data
     * form fields:
     *  - file: MultipartFile
     *  - tripId: Long
     *  - captureType: String (film|normal)
     *  - comment: String (optional)
     *  - takenAt: String(ISO-8601) (optional)
     *  - isSharedInAlbum: Boolean (optional)
     *  - expiration: String(ISO-8601) (optional)
     */
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<MediaAssetRes>> uploadPhoto(
            @RequestPart("file") MultipartFile file,
            @RequestPart("meta") CreateMediaAssetReq meta // JSON 파트: 위 필드 포함
    ) {
        MediaAssetRes res = mediaAssetService.createMediaAssetFromMultipart(file, meta, ContentType.PHOTO);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    /**
     * 릴 아이템용 영상 업로드: multipart/form-data
     * form fields:
     *  - file: MultipartFile (video/*)
     *  - meta: CreateMediaAssetReq (tripId, captureType=video, comment 등)
     *  - reelMeta: CreateReelItemReq (holdMs, transition 등 릴-아이템 정보)
     *
     * 업로드 → MediaAsset 생성 → ShortReelService로 릴 아이템 생성 → ReelItemRes 반환
     */
    @PostMapping(
            value = "/upload/reelItem",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<ReelItemRes>> uploadReelItem(
            @RequestPart("file") MultipartFile file,
            @RequestPart("meta") CreateReelItemReq req
    ) {
        ReelItemRes reelItemRes = shortReelService.createReelItem(file,req);

        return ResponseEntity.ok(ApiResponse.ok(reelItemRes));
    }

    @PostMapping(
            value = "/upload/scrapbook",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<ScrapbookRes>> uploadScrapbook(
            @RequestPart("file") MultipartFile file,
            @RequestPart("meta") CreateScrapbookReq req
    ) {
        ScrapbookRes scrapbookRes = scrapbookService.createScrapbook(file, req);
        return ResponseEntity.ok(ApiResponse.ok(scrapbookRes));
    }
}

