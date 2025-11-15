package yeohaenggasijo.tripshot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2")
public record R2Props (
        String accountId,
        String bucket,
        String accessKeyId,
        String secretAccessKey,
        String endpoint
){
}
