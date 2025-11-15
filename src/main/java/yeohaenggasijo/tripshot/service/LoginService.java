package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.user.SocialAccount;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.login.res.TokenRes;
import yeohaenggasijo.tripshot.dto.trip.res.SocialAccountInfo;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;
import yeohaenggasijo.tripshot.repository.SocialAccountRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;
import yeohaenggasijo.tripshot.security.jwt.JwtUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OtpService otpService; // 이미 있다면 사용, 없다면 간단 인터페이스로 주입
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    /** 1단계: 소셜 로그인 시도 → 있으면 access, 없으면 signup 토큰(소셜 name/profile 포함) */
    @Transactional(readOnly = true)
    public TokenRes tryLogin(SocialAccountInfo info) {
        String provider = info.getProvider();
        String socialId = info.getSocialId();

        return socialAccountRepository.findByProviderAndSocialId(provider, socialId)
                .map(sa -> new TokenRes(
                        "access",
                        jwtUtil.generateAccessToken(sa.getUser().getId(), 60),
                        sa.getUser()
                ))
                .orElseGet(() -> new TokenRes(
                        "signup",
                        // name/profile 포함해서 signup 토큰 발급
                        jwtUtil.generateSignupToken(
                                SocialLoginType.valueOf(provider),
                                socialId,
                                false,
                                null,
                                30,
                                info.getName(),
                                null // SocialAccountInfo 에 없다면 null 허용
                        ),
                        null
                ));
    }

    @Transactional
    public TokenRes verifyPhoneAndRefreshSignupToken(String signupJwt, String phone, String code) {
        // 1) 토큰 레벨 확인 (SIGNUP 이어야 함)
        jwtUtil.requireLevel(signupJwt, "signup");

        // 2) OTP 검증
        if (!otpService.verifyCode(phone, code)) {
            throw new IllegalArgumentException("인증번호가 올바르지 않습니다.");
        }

        // 3) SIGNUP 토큰 파싱 (provider/sid/name/pfp 등)
        var payload = jwtUtil.parseAndValidate(signupJwt);
        var provider = payload.provider().name(); // SocialLoginType → String
        var socialId = payload.socialId();
        var name     = payload.name();           // nullable
        var pfp      = payload.profileImageUrl();// nullable

        // 4) 전화번호로 기존 유저 조회
        Optional<User> existingOpt = userRepository.findByMobile(phone);

        if (existingOpt.isPresent()) {
            User user = existingOpt.get();

            // 4-a) 해당 소셜 계정이 이미 다른 유저에 연결돼 있는지 확인
            Optional<SocialAccount> saOpt = socialAccountRepository.findByProviderAndSocialId(provider, socialId);
            if (saOpt.isPresent() && !saOpt.get().getUser().getId().equals(user.getId())) {
                // 보안상 다른 유저에 묶여 있으면 거부
                throw new IllegalStateException("이미 다른 계정에 연결된 소셜 계정입니다.");
            }

            // 4-b) 아직 연결이 안 됐다면 현재 유저에 소셜 연결 생성 (멱등)
            if (saOpt.isEmpty()) {
                SocialAccount link = SocialAccount.builder()
                        .user(user)
                        .provider(provider)
                        .socialId(socialId)
                        .build();
                socialAccountRepository.save(link);
            }

            // 4-c) ACCESS 토큰 발급 + User 포함하여 반환 → 프론트는 바로 홈으로
            String access = jwtUtil.generateAccessToken(user.getId(), 60);
            return new TokenRes("access", access, user);
        }

        // 5) 기존 유저가 없다 → SIGNUP 계속 진행해야 함
        //    phone/phoneVerified=true 반영하여 SIGNUP 토큰 재발급 (소셜 name/pfp는 유지)
        String refreshed = jwtUtil.upgradeSignupTokenWithPhone(signupJwt, phone, 30);
        return new TokenRes("signup", refreshed, null);
    }


    /** 2-2단계: signup 최종 완료 (tag 포함) → User 생성/연결 + access 토큰 발급 */
    @Transactional
    public TokenRes completeSignupWithSignupToken(String signupJwt, String tag) {
        jwtUtil.requireLevel(signupJwt, "signup");
        var c = jwtUtil.parse(signupJwt);

        String provider = (String) c.get("prov");
        String socialId = (String) c.get("sid");
        Boolean phoneVerified = (Boolean) c.get("phv");
        String phone = (String) c.get("phone");
        String name = (String) c.get("name");
        String profileImageUrl = (String) c.get("profileImageUrl");
        logger.info("[DEBUG] token parsed: {}", c);
        if (phoneVerified == null || !phoneVerified || phone == null) {

            throw new IllegalStateException("전화번호 인증이 필요합니다.");
        }
        if (socialAccountRepository.existsByProviderAndSocialId(provider, socialId)) {
            throw new IllegalStateException("이미 연결된 소셜 계정입니다.");
        }

        // 1) 전화번호로 기존 유저 조회
        Optional<User> existing = userRepository.findByMobile(phone);

        // 2) 있으면 기존 유저 사용, 없으면 소셜 name/profile/tag로 생성
        User user = existing.orElseGet(() -> userRepository.save(
                User.builder()
                        .username(name)                 // 소셜 이름
                        .mobile(phone)                  // 인증된 폰
                        .avatarUrl(profileImageUrl)     // 소셜 프로필
                        .tag(tag)                       // 요청 바디에서 전달
                        .build()
        ));

        // 3) 소셜 계정 연결
        SocialAccount sa = SocialAccount.builder()
                .user(user)
                .provider(provider)
                .socialId(socialId)
                .build();
        socialAccountRepository.save(sa);

        // 4) access 토큰 발급
        return new TokenRes("access", jwtUtil.generateAccessToken(user.getId(), 60), user);
    }
}
