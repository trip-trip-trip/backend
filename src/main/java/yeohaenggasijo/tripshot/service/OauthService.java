package yeohaenggasijo.tripshot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yeohaenggasijo.tripshot.dto.login.res.TokenRes;
import yeohaenggasijo.tripshot.dto.login.res.TryLoginRes;
import yeohaenggasijo.tripshot.dto.trip.res.SocialAccountInfo;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;
import yeohaenggasijo.tripshot.repository.SocialAccountRepository;
import yeohaenggasijo.tripshot.service.social.SocialOauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service

public class OauthService {
    private final List<SocialOauth> socialOauthList;
    private static final Logger logger = LoggerFactory.getLogger(OauthService.class);
    private final SocialAccountRepository socialAccountRepository;
    private final LoginService loginService;

    public OauthService(List<SocialOauth> socialOauthList, SocialAccountRepository socialAccountRepository, LoginService loginService) {
        this.socialOauthList = socialOauthList;
        logger.info("[OauthService] 주입된 SocialOauth 개수: {}", socialOauthList.size());
        socialOauthList.forEach(oauth ->
                logger.info("[OauthService] 등록된 OAuth: {}", oauth.getClass().getSimpleName())
        );
        this.socialAccountRepository = socialAccountRepository;
        this.loginService = loginService;
    }

    // 소셜 로그인 요청 URL을 반환
    public String request(SocialLoginType socialLoginType) {
        logger.info("[OauthService] request 메서드 시작. Type: {}", socialLoginType);
        logger.info("[OauthService] socialOauthList 크기: {}", socialOauthList.size());

        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        logger.info("[OauthService] SocialOauth 찾기 완료: {}", socialOauth.getClass().getSimpleName());

        String url = socialOauth.getOauthRedirectURL();
        logger.info("[OauthService] URL 생성 완료: {}", url);

        return url;
    }

    // 인증 코드로 엑세스 토큰을 요청하는 메서드
    public String requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.requestAccessToken(code);
    }

    // JSON에서 엑세스 토큰만 추출
    private String extractAccessTokenFromJson(String accessTokenJson) {
        // JSON 파싱해 엑세스 토큰만 추출
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(accessTokenJson);
            return jsonNode.get("access_token") != null ? jsonNode.get("access_token").asText() : null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public TokenRes getParsedSocialAccountInfo(SocialLoginType socialLoginType, String code) {
        logger.info("[OauthService] 소셜 타입: {}로 사용자 정보 파싱 시작.", socialLoginType.name());

        // 엑세스 토큰을 포함한 JSON 응답 요청
        String accessTokenJson = this.requestAccessToken(socialLoginType, code);

        // JSON에서 엑세스 토큰만 추출
        String accessToken = extractAccessTokenFromJson(accessTokenJson);

        if (accessToken  == null) {
            logger.error("[OauthService] 액세스 토큰 추출 실패. JSON: {}", accessTokenJson);
            throw new RuntimeException("액세스 토큰 추출 실패. 소셜 로그인 실패");
        }

        // 사용자 정보를 파싱해 User 객체 생성
        String socialInfo = getSocialInfo(socialLoginType, accessToken);

        SocialAccountInfo socialAccountInfo = parseSocialInfo(socialInfo, socialLoginType, accessToken);

        return loginService.tryLogin(socialAccountInfo);
    }

    private String getSocialInfo(SocialLoginType socialLoginType, String accessToken) {
        switch (socialLoginType) {
            case GOOGLE:
                return googleApiCall(accessToken);
            case KAKAO:
                return kakaoApiCall(accessToken);
            case NAVER:
                return naverApiCall(accessToken);
            default:
                throw new IllegalArgumentException("지원되지 않는 소셜 로그인 타입입니다.");
        }
    }

    private String googleApiCall(String accessToken) {
        try {
            // accessToken URL 인코딩
            String encodedAccessToken = URLEncoder.encode(accessToken, "UTF-8");
            logger.debug("Encoded access token: {}", encodedAccessToken);

            String url = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + encodedAccessToken;
            logger.debug("Google API URL: {}", url);

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            int responseCode = con.getResponseCode();
            logger.info("Google API response code: {}", responseCode);

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                logger.info("Successfully received response from Google API.");
                return response.toString();
            } else {
                // 실패 시 에러 메시지와 상태 코드 출력
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer errorResponse = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                in.close();
                logger.error("Google API call failed with response code: {}, error: {}", responseCode, errorResponse.toString());
                throw new RuntimeException("Google API에서 사용자 정보를 가져오는 데 실패했습니다. 응답 코드: " + responseCode + ", 에러 메시지: " + errorResponse.toString());
            }
        } catch (IOException e) {
            logger.error("Google API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Google API 호출 중 오류 발생", e);

        }
    }

    private String kakaoApiCall(String accessToken) {
        try {
            String url = "https://kapi.kakao.com/v2/user/me";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                throw new RuntimeException("Kakao API에서 사용자 정보를 가져오는 데 실패했습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("kakao API 호출 중 오류 발샐", e);
        }
    }

    private String naverApiCall(String accessToken) {
        try {
            String url = "https://openapi.naver.com/v1/nid/me";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                throw new RuntimeException("Naver API에서 사용자 정보를 가져오는 데 실패했습니다. 응답코드: " + responseCode);

            }
        } catch (IOException e) {
            throw new RuntimeException("Naver API 호출 중 오류 발생", e);
        }
    }

    private SocialAccountInfo parseSocialInfo(String socialInfo, SocialLoginType socialLoginType, String accessToken) {
        JsonObject jsonObject = JsonParser.parseString(socialInfo).getAsJsonObject();

        // socialId와 name을 소셜 로그인 타입별 분리
        String socialId ="";
        String name = "";

        if (socialLoginType == SocialLoginType.GOOGLE) {
            socialId = jsonObject.get("sub").getAsString();
            name = jsonObject.get("name").getAsString();
        } else if (socialLoginType == SocialLoginType.KAKAO) {
            socialId =jsonObject.get("id").getAsString();
            name = jsonObject.getAsJsonObject("properties").get("nickname").getAsString();
        } else if (socialLoginType == SocialLoginType.NAVER) {
            JsonObject response = jsonObject.getAsJsonObject("response");
            socialId = response.get("id").getAsString();
            name = response.get("name").getAsString();
        }

        logger.info("[OauthService] 파싱 완료 - socialId: {}, name: {}", socialId, name);

        return SocialAccountInfo.builder()
                .socialId(socialId)
                .provider(socialLoginType.name())
                .accessToken(accessToken)
                .name(name)
                .build();
    }

    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }
}

