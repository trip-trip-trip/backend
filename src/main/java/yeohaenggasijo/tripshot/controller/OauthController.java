package yeohaenggasijo.tripshot.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.dto.trip.res.SocialAccountInfo;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;
import yeohaenggasijo.tripshot.service.OauthService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/login") // <--- 베이스 경로 설정
@Slf4j
public class OauthController {
    private final OauthService oauthService;

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
    public ResponseEntity<SocialAccountInfo> naverCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) { // 네이버는 state를 함께 줌

        log.info("[Controller] 네이버로부터 받은 code: {}", code);

        // 액세스 토큰 획득 후, DTO로 사용자 정보 파싱까지만 수행 (DB 저장 X)
        SocialAccountInfo socialInfo = oauthService.getParsedSocialAccountInfo(SocialLoginType.NAVER, code);

        log.info("[Controller] DTO 파싱 완료. SocialId: {}", socialInfo.getSocialId());

        return ResponseEntity.ok(socialInfo);
    }

    @GetMapping(value = "/kakao")
    public ResponseEntity<SocialAccountInfo> kakaoCallback(
            @RequestParam("code") String code) {

        log.info("[Controller] 카카오로부터 받은 code: {}", code);

        // 액세스 토큰 획득 후, DTO로 사용자 정보 파싱까지만 수행 (DB 저장 X)
        SocialAccountInfo socialInfo = oauthService.getParsedSocialAccountInfo(SocialLoginType.KAKAO, code);

        log.info("[Controller] DTO 파싱 완료. SocialId: {}", socialInfo.getSocialId());

        return ResponseEntity.ok(socialInfo);
    }

    @GetMapping(value = "/google")
    public ResponseEntity<SocialAccountInfo> googleCallback(
            @RequestParam("code") String code) {

        log.info("[Controller] 구글로부터 받은 code: {}", code);

        // 액세스 토큰 획득 후, DTO로 사용자 정보 파싱까지만 수행 (DB 저장 X)
        SocialAccountInfo socialInfo = oauthService.getParsedSocialAccountInfo(SocialLoginType.GOOGLE, code);

        log.info("[Controller] DTO 파싱 완료. SocialId: {}", socialInfo.getSocialId());

        return ResponseEntity.ok(socialInfo);
    }
}

