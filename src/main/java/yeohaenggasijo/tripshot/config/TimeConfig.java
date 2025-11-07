package yeohaenggasijo.tripshot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

// Clock 빈 등록(운영/개발 동일). 테스트에서는 FixedClock으로 교체
@Configuration
class TimeConfig {
    @Bean
    Clock clock() { return Clock.systemDefaultZone(); }
}
