package yeohaenggasijo.tripshot.config;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;

@Configuration
public class WebPushConfig {

    // BouncyCastle Provider 등록 (VAPID 키 암호화를 위해 필수)
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Bean
    public PushService pushService(VapidKeys vapidKeys) {
        try {
            // PushService 생성자가 던지는 예외를 처리
            return new PushService(
                    vapidKeys.getPublicKey(),
                    vapidKeys.getPrivateKey(),
                    vapidKeys.getSubject()
            );
        } catch (GeneralSecurityException e) {
            System.err.println("❌❌❌ ERROR: VAPID Key Load Failed! Check the key string for unexpected characters or incorrect Base64 URL Safe format. ❌❌❌");
            // 보안/IO 예외 발생 시 애플리케이션 시작을 막고 로그 출력
            throw new RuntimeException("Error initializing PushService with VAPID keys", e);
        }
    }
}
