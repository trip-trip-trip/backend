package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.user.SocialAccount;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.login.res.TokenRes;
import yeohaenggasijo.tripshot.dto.login.res.TryLoginRes;
import yeohaenggasijo.tripshot.dto.trip.res.SocialAccountInfo;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;
import yeohaenggasijo.tripshot.repository.SocialAccountRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;
import yeohaenggasijo.tripshot.security.jwt.JwtUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Service
@RequiredArgsConstructor
public class LoginService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /** 1단계: 소셜 로그인 시도 */
    @Transactional(readOnly = true)
    public TokenRes tryLogin(SocialAccountInfo info) {
        String provider = info.getProvider();
        String socialId = info.getSocialId();

        return socialAccountRepository.findByProviderAndSocialId(provider, socialId)
                .map(sa -> new TokenRes("access", jwtUtil.generateAccessToken(sa.getUser().getId(), 60)))
                // 없으면 'signup' 토큰 발급 → 이후 전화번호 검증 + completeSignup 으로 이어짐
                .orElseGet(() -> new TokenRes("signup",
                        jwtUtil.generateSignupToken(SocialLoginType.valueOf(provider), socialId, false, null, 15)));
    }

    /** 2단계: 가입완료 — 전화번호로 유저 있는지 확인 후, SocialAccount 연결/생성 + access 토큰 발급 */
    @Transactional
    public String completeSignupAndIssueAccess(SocialLoginType provider, String socialId,
                                               String phone, String username, String email) {
        // 이미 다른 유저와 연결되어 있는지 방지
        if (socialAccountRepository.existsByProviderAndSocialId(provider.name(), socialId)) {
            throw new IllegalStateException("이미 연결된 소셜 계정입니다.");
        }

        // 1) 전화번호로 기존 유저 조회
        Optional<User> existing = userRepository.findByMobile(phone);

        // 2) 있으면 그 유저에 소셜 연결, 없으면 유저 생성 후 연결
        User user = existing.orElseGet(() -> userRepository.save(
                User.builder()
                        .username(username)
                        .email(email)
                        .mobile(phone)
                        .build()
        ));

        SocialAccount sa = SocialAccount.builder()
                .user(user)
                .provider(provider.name())
                .socialId(socialId)
                .build();
        socialAccountRepository.save(sa);

        // 3) access 토큰 발급
        return jwtUtil.generateAccessToken(user.getId(), 60);
    }
}