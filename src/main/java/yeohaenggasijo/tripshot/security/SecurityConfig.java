package yeohaenggasijo.tripshot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import yeohaenggasijo.tripshot.security.jwt.JwtAuthFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DevHeaderAuthFilter devHeaderAuthFilter;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //TODO: 배포하기 전 production 환경으로 정책 전환
        http.csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/login/**", "/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/posts/**").permitAll()
                        .requestMatchers("/login/signup/verify-and-complete").hasRole("SIGNUP")
                        .anyRequest().authenticated())
                .addFilterBefore(devHeaderAuthFilter, org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, AnonymousAuthenticationFilter.class);
        http.addFilterBefore(devHeaderAuthFilter, JwtAuthFilter.class);
        return http.build();
    }
}
