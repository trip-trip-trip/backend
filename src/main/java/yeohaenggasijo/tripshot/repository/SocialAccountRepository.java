package yeohaenggasijo.tripshot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeohaenggasijo.tripshot.domain.user.SocialAccount;
import yeohaenggasijo.tripshot.domain.user.User;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndSocialId(String provider, String socialId);
    boolean existsByProviderAndSocialId(String provider, String socialId);
}
