package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.dto.reel.CreateReelReq;
import yeohaenggasijo.tripshot.dto.reel.ReelRes;
import yeohaenggasijo.tripshot.dto.scrapbook.ScrapbookRes;
import yeohaenggasijo.tripshot.service.MediaAssetService;
import yeohaenggasijo.tripshot.service.ShortReelService;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaAssetController {

    private final MediaAssetService mediaAssetService;
    private final ShortReelService shortReelService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(@RequestParam String type, @RequestBody CreateMediaAssetReq req){
        return ResponseEntity.ok(ApiResponse.ok(mediaAssetService.createMediaAsset(req)));
    }

    @PostMapping("/upload/reel")
    public ResponseEntity<ApiResponse<ReelRes>> uploadReel(@RequestBody CreateReelReq req) {
        return ResponseEntity.ok(ApiResponse.ok(shortReelService.createReel(req)));
    }

//    @PostMapping("/upload/scrapbook")
//    public ResponseEntity<ApiResponse<ScrapbookRes>> uploadScrapbook(@RequestBody CreateScrapbookReq req) {
//        return ResponseEntity.ok(ApiResponse.ok(ScrapbookService.createScrapbook(req)));
//
//    }
}
