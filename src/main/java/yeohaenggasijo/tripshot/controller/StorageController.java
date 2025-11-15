// src/main/java/.../controller/StorageController.java
package yeohaenggasijo.tripshot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.storage.R2StorageService;
import yeohaenggasijo.tripshot.dto.media.*;

// (보안) 인증 필요: @PreAuthorize or SecurityConfig에서 보호
@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final R2StorageService storageService;
    private final CurrentUserProvider currentUser; // 앞서 구성한 유저 컨텍스트

    @PostMapping("/uploads")
    public ResponseEntity<UploadRes> createUploadUrl(@Valid @RequestBody UploadReq req) {
        Long userId = currentUser.requireUserId();

        // 파일 종류 검증 (이미지 MIME만 허용 예시)
        if (!req.contentType().startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }

        String key = storageService.buildObjectKey(userId, req.tripId(), req.fileName());
        var pre = storageService.createPresignedPutUrl(key, req.contentType(), 600); // 10분

        // 퍼블릭 도메인 사용 시 즉시 접근 가능한 URL (선택)
//        Optional<String> publicUrl = storageService.publicUrl(key);
        // 비공개 버킷이면 presigned GET 제공(선택)
        String getUrl = storageService.createPresignedGetUrl(key, 600);

        UploadRes resp = new UploadRes(
                key,
                pre.url(),
                pre.headers(),
                null,
                getUrl
        );
        return ResponseEntity.ok(resp);
    }
}
