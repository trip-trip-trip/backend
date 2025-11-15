package yeohaenggasijo.tripshot.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.repository.UserRepository;

@Profile({"test"})
@Configuration
@RequiredArgsConstructor
public class DevSeedRunner {
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner seedUser() {
        return args -> {
            userRepository.findById(1L).orElseGet(() ->
                    userRepository.save(User.builder()
                            .username("dev-user")
                            .tag("a1s2d3f4")
                            .passwordHash("pw")
                            .bio("dev seed user")
                            .avatarUrl(null)
                            .build())
            );
        };
    }
}
