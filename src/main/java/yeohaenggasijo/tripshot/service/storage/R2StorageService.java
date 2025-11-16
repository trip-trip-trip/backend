// src/main/java/.../service/storage/R2StorageService.java
package yeohaenggasijo.tripshot.service.storage;

import lombok.RequiredArgsConstructor;
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
        this.bucket = bucket;
        this.publicBaseUrl = trimTrailingSlash(publicBaseUrl);

        this.s3 = S3Client.builder()
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto")) // R2는 무시되지만 필수 파라미터
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // R2는 path-style 권장
                        .build())
                .build();
    }

    /** Path 기반 업로드 (다른 레이어에서 가장 쓰기 좋음) */
    public StorageUploader.UploadResult upload(Path file, String contentType, String objectKey) {
        try {
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

            return new StorageUploader.UploadResult(objectKey, url, etag, size);
        } catch (IOException e) {
            throw new RuntimeException("R2 upload failed: " + objectKey, e);
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

