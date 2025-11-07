package yeohaenggasijo.tripshot.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.login.req.OtpSendReq;
import yeohaenggasijo.tripshot.dto.login.req.OtpVerifyReq;
import yeohaenggasijo.tripshot.dto.login.req.SignupCompleteReq;
import yeohaenggasijo.tripshot.dto.login.req.VerifyAndCompleteReq;
import yeohaenggasijo.tripshot.dto.login.res.TokenRes;
import yeohaenggasijo.tripshot.dto.login.res.TokenRes;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;
import yeohaenggasijo.tripshot.security.jwt.JwtUtil;
import yeohaenggasijo.tripshot.security.jwt.TokenLevel;
import yeohaenggasijo.tripshot.service.LoginService;
import yeohaenggasijo.tripshot.service.OauthService;
import yeohaenggasijo.tripshot.service.OtpService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/login") // <--- 베이스 경로 설정
@Slf4j
public class OauthController {
    private final OauthService oauthService;
    private final LoginService loginService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    // 1. 소셜 로그인 시작
    @GetMapping("/start/{socialLoginType}")
    public void startLogin(
            @PathVariable String socialLoginType, // String으로 받고
            HttpServletResponse response) throws IOException {

        try {
            log.info("========== 로그인 시작 ==========");
            log.info("[Controller] 요청 받은 socialLoginType: {}", socialLoginType);

            SocialLoginType type = SocialLoginType.valueOf(socialLoginType.toUpperCase());
            log.info("[Controller] Enum 변환 완료: {}", type.name());

            String redirectUrl = oauthService.request(type);
            log.info("[Controller] Redirect URL 생성 완료: {}", redirectUrl);

            response.sendRedirect(redirectUrl);
            log.info("[Controller] 리다이렉트 완료");

        } catch (Exception e) {
            log.error("[Controller] 오류 발생", e);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }

    // 2. 콜백 처리
    @GetMapping(value = "/naver")
    public ResponseEntity<ApiResponse<TokenRes>> naverCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) { // 네이버는 state를 함께 줌

        log.info("[Controller] 네이버로부터 받은 code: {}", code);

        // 액세스 토큰 획득 후, DTO로 사용자 정보 파싱까지만 수행 (DB 저장 X)
        TokenRes tokenRes = oauthService.getParsedSocialAccountInfo(SocialLoginType.NAVER, code);
//        if(tokenRes.success()) {
//            return ResponseEntity.ok(ApiResponse.ok(tokenRes.token()));
//        }else {
//            return ResponseEntity.ok(new ApiResponse(true, 200, "user not found: please proceed to phone verification", null));
//        }
        return ResponseEntity.ok(ApiResponse.ok(tokenRes));
    }

    @GetMapping(value = "/kakao")
    public ResponseEntity<ApiResponse<?>> kakaoCallback(
            @RequestParam("code") String code) {

        log.info("[Controller] 카카오로부터 받은 code: {}", code);

        // 액세스 토큰 획득 후, DTO로 사용자 정보 파싱까지만 수행 (DB 저장 X)
        TokenRes tokenRes = oauthService.getParsedSocialAccountInfo(SocialLoginType.KAKAO, code);
//        if(tokenRes.success()) {
//            return ResponseEntity.ok(ApiResponse.ok(tokenRes.token()));
//        }else {
//            return ResponseEntity.ok(new ApiResponse(true, 200, "user not found: please proceed to phone verification", null));
//        }
        // log.info("[Controller] DTO 파싱 완료. SocialId: {}", socialInfo.getSocialId());
        return ResponseEntity.ok(ApiResponse.ok(tokenRes));
    }

    @GetMapping(value = "/google")
    public ResponseEntity<ApiResponse<TokenRes>> googleCallback(
            @RequestParam("code") String code) {

        log.info("[Controller] 구글로부터 받은 code: {}", code);

        // 액세스 토큰 획득 후, DTO로 사용자 정보 파싱까지만 수행 (DB 저장 X)
        TokenRes tokenRes = oauthService.getParsedSocialAccountInfo(SocialLoginType.GOOGLE, code);
//        if (tokenRes.success()) {
//            return ResponseEntity.ok(ApiResponse.ok(tokenRes.token()));
//        }else {
//            return ResponseEntity.ok(new ApiResponse(true, 200, "user not found: please proceed to phone verification", null));
//        }
        return ResponseEntity.ok(ApiResponse.ok(tokenRes));
    }

    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<?>> sendCode(@Valid @RequestBody OtpSendReq req) {
        String phone = req.phone();
        otpService.sendVerificationCode(phone);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Verification code sent", null));
    }

//    @PostMapping("/verify-code")
//    public ResponseEntity<ApiResponse<?>> verify(@RequestBody OtpVerifyReq req,
//                                                        @RequestHeader("Authorization") String authz) {
//        String token = authz.substring(7);
//        var p = jwtUtil.parseAndValidate(token);
//        if (p.level() != TokenLevel.SIGNUP) {
//            return ResponseEntity.status(403).body(ApiResponse.error(403, "Wrong token level"));
//        }
//        boolean v = loginService.verifyCode(req.phone(), req.code());
//        if (v) {
//            String newToken = jwtUtil.generateSignupToken(p.provider(), p.socialId(), true, req.phone(), 15);
//            return ResponseEntity.ok(ApiResponse.ok(new TokenRes(newToken, "signup")));
//        }
//        return ResponseEntity.ok(new ApiResponse<>(v, 200, v? "code verified" : "verification failed. please check the code", null));
//    }
//
//    // 가입 완료
//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse<?>> complete(@RequestBody SignupCompleteReq req,
//                                                          @RequestHeader("Authorization") String authz) {
//        var p = jwtUtil.parseAndValidate(authz.substring(7));
//        if (p.level() != TokenLevel.SIGNUP || !p.phoneVerified()) {
//            return ResponseEntity.status(403).body(ApiResponse.error(403, "Phone not verified"));
//        }
//        String access = loginService.completeSignupAndIssueAccess(
//                p.provider(), p.socialId(), p.phone(), req.username(), req.email()
//        );
//        return ResponseEntity.ok(ApiResponse.ok(new TokenRes("access", access)));
//    }

    @PostMapping("/signup/verify-and-complete")
    public ResponseEntity<ApiResponse<?>> verifyAndComplete(
            @Valid @RequestBody VerifyAndCompleteReq req,
            @RequestHeader("Authorization") String authz
    ) {
        var payload = jwtUtil.parseAndValidate(authz.substring(7));
        if (payload.level() != TokenLevel.SIGNUP) {
            return ResponseEntity.status(403).body(ApiResponse.error(403, "Wrong token level"));
        }

        // 1) 전화번호 코드 검증 (실패시 즉시 400)
        boolean ok = otpService.verifyCode(req.phone(), req.code());
        if (!ok) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Invalid phone verification code"));
        }

        // 2) 가입/연동 + access 토큰 발급 (서비스 내부에서: 전화번호 유저 조회 → 있으면 연결, 없으면 가입 후 연결)
        String access = loginService.completeSignupAndIssueAccess(
                SocialLoginType.valueOf(payload.provider().name()),
                payload.socialId(),
                req.phone(),
                req.username(),
                req.email()
        );

        return ResponseEntity.ok(ApiResponse.ok(new TokenRes("access", access)));
    }
}

