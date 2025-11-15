// src/main/java/.../service/storage/R2StorageService.java
package yeohaenggasijo.tripshot.service.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.*;
import software.amazon.awssdk.services.s3.presigner.model.*;
import yeohaenggasijo.tripshot.config.R2Props;

import java.net.URL;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class R2StorageService {

    private final R2Props props;
    private final S3Client s3;
    private final S3Presigner presigner;

    /** 업로드용 Key 생성 규칙 (userId/tripId/yyyy/MM/uuid.ext) */
    public String buildObjectKey(Long userId, Long tripId, String originalFilename) {
        String ext = Optional.ofNullable(originalFilename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(originalFilename.lastIndexOf('.')))
                .orElse("");
        var cal = java.time.ZonedDateTime.now();
        String y = String.valueOf(cal.getYear());
        String m = String.format("%02d", cal.getMonthValue());
        return "%d/%d/%s/%s/%s%s".formatted(
                userId, tripId, y, m, UUID.randomUUID(), ext
        );
    }

    /** PUT Presigned URL 생성 */
    public PresignedPutResponse createPresignedPutUrl(String key, String contentType, long expiresSeconds) {
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .contentType(contentType) // 브라우저에서 동일하게 보내야 함
                .build();

        PresignedPutObjectRequest pre = presigner.presignPutObject(b -> b
                .signatureDuration(Duration.ofSeconds(expiresSeconds))
                .putObjectRequest(put)
        );

        URL url = pre.url();
        // S3 V4 서명은 특정 헤더를 같이 전송해야 할 수도 있음 → 프론트에 전달
        Map<String, List<String>> headers = pre.signedHeaders();

        return new PresignedPutResponse(url.toString(), headers);
    }

    /** GET Presigned URL 생성 (비공개 버킷에서 내려받기/썸네일 조회용) */
    public String createPresignedGetUrl(String key, long expiresSeconds) {
        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build();
        PresignedGetObjectRequest pre = presigner.presignGetObject(b -> b
                .signatureDuration(Duration.ofSeconds(expiresSeconds))
                .getObjectRequest(get)
        );
        return pre.url().toString();
    }

    /** 객체 존재 확인(업로드 완료 후 검증용) */
    public boolean exists(String key) {
        try {
            s3.headObject(HeadObjectRequest.builder()
                    .bucket(props.bucket())
                    .key(key)
                    .build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    /** (선택) 삭제 */
    public void delete(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build());
    }

    /** 퍼블릭 경로를 쓰는 경우 (CDN/커스텀 도메인) */
//    public Optional<String> publicUrl(String key) {
//        return Optional.ofNullable(props.publicBaseUrl())
//                .filter(u -> !u.isBlank())
//                .map(u -> u.endsWith("/") ? u + key : u + "/" + key);
//    }

    public record PresignedPutResponse(
            String url,
            Map<String, List<String>> headers // 프론트가 그대로 붙여서 PUT
    ) {}
}
