package yeohaenggasijo.tripshot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174",
                "https://tripshot.duckdns.org",
                "https://tripshot.vercel.app"  // 필요 시 추가
        ));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "X-Requested-With",
                "X-User-Id" // dev 헤더 등을 쓰면 여기에 추가
        ));
        c.setExposedHeaders(List.of(
                "Location" // 필요 시 클라이언트에서 읽게 할 헤더
        ));
        c.setAllowCredentials(true); // 쿠키/Authorization 헤더 사용할 경우
        c.setMaxAge(3600L); // preflight 캐시(초)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
