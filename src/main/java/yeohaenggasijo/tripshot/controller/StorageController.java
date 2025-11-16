// src/main/java/.../controller/StorageController.java
package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.service.storage.R2StorageService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class StorageController {

    private final R2StorageService r2; // 컨트롤러는 서비스에만 의존

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<UploadRes>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 1) 임시파일로 저장
        String ext = getExt(file.getOriginalFilename());
        Path temp = Files.createTempFile("upload_", ext);

        file.transferTo(temp.toFile());

        // 2) 객체키 생성 (폴더 전략은 서비스 정책에 맞게)
        String key = "uploads/" + UUID.randomUUID() + ext;

        // 3) 서비스 업로드
        var result = r2.upload(temp, file.getContentType(), key);

        // 4) 응답
        UploadRes body = new UploadRes(result.key(), result.url(), result.etag(), result.size());
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    private String getExt(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return (i >= 0) ? name.substring(i) : "";
    }

    public record UploadRes(String key, String url, String etag, long size) {}
}