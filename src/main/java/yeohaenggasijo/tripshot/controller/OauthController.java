package yeohaenggasijo.tripshot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.login.req.OtpSendReq;
import yeohaenggasijo.tripshot.dto.login.req.OtpVerifyReq;
import yeohaenggasijo.tripshot.dto.login.req.SignupReq; // 신규: tag만 받는 DTO
import yeohaenggasijo.tripshot.dto.login.res.TokenRes;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;
import yeohaenggasijo.tripshot.service.LoginService;
import yeohaenggasijo.tripshot.service.OauthService;
import yeohaenggasijo.tripshot.service.OtpService;
import yeohaenggasijo.tripshot.security.jwt.JwtUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class OauthController {

    private final OauthService oauthService;
    private final LoginService loginService;
    private final OtpService otpService; // 기존 사용중이면 그대로
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 0) 프론트에서 /login/{provider} 로 진입 → 소셜 권한 부여 페이지로 302 */
    @GetMapping("/start/{provider}")
    public void oauthAuthorize(@PathVariable("provider") SocialLoginType provider, HttpServletResponse res) throws IOException {
        String redirectUrl = oauthService.request(provider);
        res.sendRedirect(redirectUrl);
    }

    /** 1) 콜백: 카카오/네이버/구글 공통 처리 → level에 따라 프론트 도메인으로 302 */
    @GetMapping("/{provider}")
    public void oauthCallback(@PathVariable("provider") SocialLoginType provider,
                              @RequestParam("code") String code,
                              HttpServletResponse res) throws IOException {

        TokenRes tokenRes = oauthService.getParsedSocialAccountInfo(provider, code);
        String target = "signup".equals(tokenRes.level())
                ? "https://tripshot.vercel.app/phone"
                : "https://tripshot.vercel.app/home";

        // 기존 그대로: level, token, user 전달 유지 (user는 JSON→url encode)
        String userJson = tokenRes.user() == null ? "" : objectMapper.writeValueAsString(tokenRes.user());

        String url = UriComponentsBuilder.fromHttpUrl(target)
                .queryParam("level", tokenRes.level())
                .queryParam("token", tokenRes.jwtToken())
                .queryParam("user", StringUtils.hasText(userJson) ? URLEncoder.encode(userJson, StandardCharsets.UTF_8) : "")
                .build()
                .toUriString();

        res.sendRedirect(url);
    }

    /** 2-0) 인증번호 발송 (그대로 사용) */
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Void>> sendCode(@RequestBody OtpSendReq req) {
        otpService.sendVerificationCode(req.phone());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** 2-1) 전화번호 인증만 수행 → signup 토큰을 phone/verified 반영하여 갱신 반환 */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<TokenRes>> verifyCode(@RequestHeader("Authorization") String authorization,
                                                            @RequestBody OtpVerifyReq req) {
        String signupJwt = authorization.replace("Bearer ", "").trim();
        TokenRes refreshed = loginService.verifyPhoneAndRefreshSignupToken(signupJwt, req.phone(), req.code());
        return ResponseEntity.ok(ApiResponse.ok(refreshed));
    }

    /** 2-2) 최종 회원가입: tag(string)만 바디로 받고 access 토큰 + 유저 반환 */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenRes>> completeSignup(@RequestHeader("Authorization") String authorization,
                                                                @RequestBody SignupReq req) {
        String signupJwt = authorization.replace("Bearer ", "").trim();
        TokenRes access = loginService.completeSignupWithSignupToken(signupJwt, req.tag());
        return ResponseEntity.ok(ApiResponse.ok(access));
    }
}
