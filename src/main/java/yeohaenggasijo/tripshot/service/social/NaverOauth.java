package yeohaenggasijo.tripshot.service.social;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NaverOauth implements SocialOauth{
    // 설정 파일에 있는 값을 읽어서 Java 변수에 넣어줌
    @Value("${sns.naver.url}")
    private String NAVER_SNS_BASE_URL;

    @PostConstruct
    public void init() {
        System.out.println("NaverOauth 초기화됨");
        System.out.println("NAVER_SNS_BASE_URL: " + NAVER_SNS_BASE_URL);
        System.out.println("NAVER_SNS_CLIENT_ID: " + NAVER_SNS_CLIENT_ID);
    }

    @Value("${sns.naver.client.id}")
    private String NAVER_SNS_CLIENT_ID;
    @Value("${sns.naver.client.secret}")
    private String NAVER_SNS_CLIENT_SECRET;
    @Value("${sns.naver.callback.url}")
    private String NAVER_SNS_CALLBACK_URL;
    @Value("${sns.naver.token.url}")
    private String NAVER_SNS_TOKEN_BASE_URL;

    // 사용자가 네이버 로그인 페이지로 이동할  최종 주소 만드는 역할
    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", NAVER_SNS_CLIENT_ID);
        params.put("redirect_uri", NAVER_SNS_CALLBACK_URL);
        params.put("state", "random_state_value");

        // 모든 파라미터 하나의 값으로 만듦
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        // 최종 URL 반환
        return NAVER_SNS_BASE_URL + "?" + parameterString;
    }

    // 엑세스 토큰 요청
    @Override
    public String requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code); // 네이버가 보낸 임시 인증 코드
        params.add("client_id", NAVER_SNS_CLIENT_ID);
        params.add("client_secret", NAVER_SNS_CLIENT_SECRET);
        params.add("redirect_uri", NAVER_SNS_CALLBACK_URL);
        params.add("grant_type", "authorization_code"); // 토큰 요청 명시
        params.add("state", "random_state_value");

        // Http Entity 생성 - 헤더와 파라미터를 하나로 묶어주는 객체
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // 요청 전송 - 네이버의 토큰 발급 서버 주소로 requestEntity를 POST 방식으로 전송, 응답은 String 형태로 받음
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(NAVER_SNS_TOKEN_BASE_URL, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody(); // 엑세스 토큰, 토큰 타입 등의 JSON 문자열 그대로 반환
        }
        return "네이버 로그인 요청 처리 실패";

    }
}
