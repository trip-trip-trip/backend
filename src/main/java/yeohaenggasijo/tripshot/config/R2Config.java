package yeohaenggasijo.tripshot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(R2Props.class)
public class R2Config {

    @Bean
    public S3Client r2S3Client(R2Props props) {
        return S3Client.builder()
                .httpClient(UrlConnectionHttpClient.builder().build())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.accessKeyId(), props.secretAccessKey())))
                .region(Region.US_EAST_1) // R2는 region 'auto' 대신 US_EAST_1 사용 + endpointOverride
                .endpointOverride(URI.create(props.endpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // R2는 path-style 권장
                        .build())
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }

    @Bean
    public S3Presigner r2Presigner(R2Props props) {
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.accessKeyId(), props.secretAccessKey())))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(props.endpoint()))
                .build();
    }
}