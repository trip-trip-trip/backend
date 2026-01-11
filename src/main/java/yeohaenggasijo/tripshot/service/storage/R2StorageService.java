// src/main/java/.../service/storage/R2StorageService.java
package yeohaenggasijo.tripshot.service.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class R2StorageService {

    private final S3Client s3;
    private final String bucket;
    private final String publicBaseUrl; // 예: https://cdn.tripshot.app  또는 https://<accountid>.r2.cloudflarestorage.com/<bucket>

    public R2StorageService(
            @Value("${r2.endpoint}") String endpoint,                 // https://<accountid>.r2.cloudflarecom
            @Value("${r2.access-key-id}") String accessKey,
            @Value("${r2.secret-access-key}") String secretKey,
            @Value("${r2.bucket}") String bucket,
            @Value("${r2.public-base-url}") String publicBaseUrl      // 위 설명 참고
    ) {
        // R2 설정 디버깅 로그
        log.info("=== R2StorageService Configuration ===");
        log.info("Endpoint: {}", endpoint);
        log.info("Bucket: {}", bucket);
        log.info("Public Base URL: {}", publicBaseUrl);
        log.info("Access Key ID: {}", accessKey != null ? accessKey.substring(0, Math.min(8, accessKey.length())) + "***" : "null");
        log.info("Secret Key: {}", secretKey != null ? "***" + secretKey.substring(Math.max(0, secretKey.length() - 4)) : "null");
        log.info("=====================================");

        this.bucket = bucket;
        this.publicBaseUrl = trimTrailingSlash(publicBaseUrl);

        this.s3 = S3Client.builder()
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1) // R2는 region을 무시하지만, auto 대신 명시적으로 지정
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // R2는 path-style 권장
                        .checksumValidationEnabled(false) // R2 호환성: checksum 검증 비활성화
                        .chunkedEncodingEnabled(false) // R2 호환성: chunked encoding 비활성화
                        .build())
                .build();
    }

    /** Path 기반 업로드 (다른 레이어에서 가장 쓰기 좋음) */
    public StorageUploader.UploadResult upload(Path file, String contentType, String objectKey) {
        try {
            log.debug("Uploading file to R2: bucket={}, key={}, contentType={}", bucket, objectKey, contentType);

            String ct = (contentType != null) ? contentType : guessContentType(file);
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(ct)
                    .build();

            var res = s3.putObject(req, RequestBody.fromFile(file));

            String url = buildPublicUrl(objectKey);
            long size = Files.size(file);
            String etag = res.eTag();

            log.info("Successfully uploaded to R2: key={}, size={}, etag={}", objectKey, size, etag);
            return new StorageUploader.UploadResult(objectKey, url, etag, size);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error("R2 S3Exception - Status: {}, Code: {}, Message: {}, RequestId: {}, Bucket: {}, Key: {}",
                    e.statusCode(), e.awsErrorDetails().errorCode(), e.awsErrorDetails().errorMessage(),
                    e.requestId(), bucket, objectKey);
            throw new RuntimeException("R2 upload failed (S3Exception): " + objectKey, e);
        } catch (IOException e) {
            log.error("R2 IOException while uploading: {}", objectKey, e);
            throw new RuntimeException("R2 upload failed (IOException): " + objectKey, e);
        }
    }

    private String guessContentType(Path file) throws IOException {
        String ct = Files.probeContentType(file);
        return (ct != null) ? ct : "application/octet-stream";
    }

    private String buildPublicUrl(String key) {
        // publicBaseUrl 이 이미 버킷까지 포함한 형태라면 그대로 붙입니다.
        return publicBaseUrl + "/" + key;
    }

    private String trimTrailingSlash(String s) {
        if (s == null) return "";
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}

