package yeohaenggasijo.tripshot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final DevHeaderAuthFilter devHeaderAuthFilter;

    public SecurityConfig(DevHeaderAuthFilter devHeaderAuthFilter) {
        this.devHeaderAuthFilter = devHeaderAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//TODO: 배포하기 전 production 환경으로 정책 전환
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll())
                .addFilterBefore(devHeaderAuthFilter, org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);
        return http.build();
    }
}
