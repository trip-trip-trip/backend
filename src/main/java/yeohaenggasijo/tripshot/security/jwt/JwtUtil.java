package yeohaenggasijo.tripshot.security.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(
            @Value("${jwt.secret}") String secret
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 일반 접근 토큰 (로그인 성공 후) */
    public String generateAccessToken(Long userId, long expMinutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("lvl", TokenLevel.ACCESS.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /** 회원가입 전용 토큰 (소셜 식별 + 휴대폰 검증 단계용) */
    public String generateSignupToken(SocialLoginType provider, String socialId,
                                      boolean phoneVerified, String phone,
                                      long expMinutes) {
        Instant now = Instant.now();
        JwtBuilder b = Jwts.builder()
                .subject("signup") // sub는 실제 userId가 아직 없음
                .claim("lvl", TokenLevel.SIGNUP.name())
                .claim("prov", provider.name())
                .claim("sid", socialId)
                .claim("phv", phoneVerified);
        if (phone != null) b.claim("phone", phone);
        return b.issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /** 검증 + 파싱 결과 DTO */
    public record Payload(TokenLevel level, Long userId,
                          SocialLoginType provider, String socialId,
                          boolean phoneVerified, String phone) {}

    public Payload parseAndValidate(String token) {
        var jws = Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
        var c = jws.getPayload();
        var lvl = TokenLevel.valueOf(c.get("lvl", String.class));
        if (lvl == TokenLevel.ACCESS) {
            Long uid = Long.parseLong(c.getSubject());
            return new Payload(TokenLevel.ACCESS, uid, null, null, false, null);
        } else {
            var prov = SocialLoginType.valueOf(c.get("prov", String.class));
            var sid  = c.get("sid", String.class);
            boolean phv = Boolean.TRUE.equals(c.get("phv", Boolean.class));
            String phone = c.get("phone", String.class);
            return new Payload(TokenLevel.SIGNUP, null, prov, sid, phv, phone);
        }
    }


}
