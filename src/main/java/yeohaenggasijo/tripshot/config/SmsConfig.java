package yeohaenggasijo.tripshot.config;

import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {

    @Value("${coolsms.api-key}")
    private String apiKey;
    @Value("${coolsms.api-secret}")
    private String apiSecret;

    // CoolSMS Message Bean 등록
    @Bean
    public DefaultMessageService messageService() {
        return new DefaultMessageService(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }
}