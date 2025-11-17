package yeohaenggasijo.tripshot.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /* =========================
     *  발급 (ACCESS / SIGNUP)
     * ========================= */

    /** ACCESS 토큰: 정상 로그인 사용자 */
    public String generateAccessToken(Long userId, long expMin) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claim("lvl", TokenLevel.ACCESS.name())
                .subject(String.valueOf(userId))          // ACCESS에서는 subject=uid
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expMin, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /** SIGNUP 토큰(기존 시그니처 유지): name/pfp 없이 */
    public String generateSignupToken(SocialLoginType provider,
                                      String socialId,
                                      boolean phoneVerified,
                                      String phone,
                                      long expMin) {
        return generateSignupToken(provider, socialId, phoneVerified, phone, expMin, null, null);
    }

    /** SIGNUP 토큰(신규): 소셜 name / profileImageUrl(pfp)까지 포함 */
    public String generateSignupToken(SocialLoginType provider,
                                      String socialId,
                                      boolean phoneVerified,
                                      String phone,
                                      long expMin,
                                      String name,
                                      String profileImageUrl) {
        Instant now = Instant.now();
        var b = Jwts.builder()
                .claim("lvl", TokenLevel.SIGNUP.name())
                .claim("prov", provider.name())
                .claim("sid", socialId)
                .claim("phv", phoneVerified)
                .claim("phone", phone)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expMin, ChronoUnit.MINUTES)));

        if (name != null) b.claim("name", name);
        if (profileImageUrl != null) b.claim("pfp", profileImageUrl);

        // subject는 ACCESS에서만 강의미가 있으므로 생략 (원하면 "signup:prov:sid" 등으로 세팅해도 무방)
        return b.signWith(key).compact();
    }

    /** verify 이후, signup 토큰에 phone/verified 반영해서 재발급 */
    public String upgradeSignupTokenWithPhone(String signupJwt, String phone, long expMin) {
        Claims c = parse(signupJwt);
        TokenLevel lvl = readLevel(c);
        if (lvl != TokenLevel.SIGNUP) {
            throw new IllegalStateException("Not a SIGNUP token");
        }

        SocialLoginType provider = SocialLoginType.valueOf(get(c, "prov"));
        String socialId = get(c, "sid");
        String name = c.get("name", String.class);
        String pfp  = c.get("pfp", String.class);

        return generateSignupToken(provider, socialId, true, phone, expMin, name, pfp);
    }

    /* =========================
     *  파싱/검증/헬퍼
     * ========================= */

    /** 서명/만료 검증 + Payload로 변환 (필터에서 사용) */
    public Payload parseAndValidate(String jwt) {
        Claims c = parse(jwt);
        TokenLevel lvl = readLevel(c);

        if (lvl == TokenLevel.ACCESS) {
            Long uid = tryParseLong(c.getSubject());
            return new Payload(TokenLevel.ACCESS, uid, null, null, false, null, null, null);
        } else { // SIGNUP
            SocialLoginType prov = SocialLoginType.valueOf(get(c, "prov"));
            String sid   = get(c, "sid");
            boolean phv  = Boolean.TRUE.equals(c.get("phv", Boolean.class));
            String phone = c.get("phone", String.class);
            String name  = c.get("name", String.class);
            String pfp   = c.get("pfp", String.class);
            return new Payload(TokenLevel.SIGNUP, null, prov, sid, phv, phone, name, pfp);
        }
    }

    /** 단순 레벨 조회 */
    public String getLevel(String jwt) {
        return readLevel(parse(jwt)).name();
    }

    /** 레벨 강제 일치 검사 (서비스 레벨에서 사용) */
    public void requireLevel(String jwt, String expected) {
        TokenLevel want = TokenLevel.valueOf(expected.toUpperCase());
        if (readLevel(parse(jwt)) != want) {
            throw new IllegalStateException("JWT level mismatch");
        }
    }

    /* =========================
     *  내부 유틸
     * ========================= */

    public Claims parse(String jwt) {
        return Jwts.parser().verifyWith((SecretKey) key).build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private TokenLevel readLevel(Claims c) {
        // 과거/미래 호환: lvl(권장) 없으면 level 키도 한 번 더 조회
        String lv = c.get("lvl", String.class);
        if (lv == null) lv = c.get("level", String.class);
        if (lv == null) throw new IllegalStateException("JWT has no level");
        return TokenLevel.valueOf(lv);
    }

    private static String get(Claims c, String key) {
        String v = c.get(key, String.class);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing claim: " + key);
        }
        return v;
    }

    private static Long tryParseLong(String s) {
        try { return s == null ? null : Long.parseLong(s); }
        catch (NumberFormatException e) { return null; }
    }

    /** 필터/서비스에서 쓰기 좋게 파싱 결과를 묶어둔 DTO */
    public record Payload(
            TokenLevel level,
            Long userId,                       // ACCESS 전용
            SocialLoginType provider,          // SIGNUP 전용
            String socialId,                   // SIGNUP 전용
            boolean phoneVerified,             // SIGNUP 전용
            String phone,                      // SIGNUP 전용
            String name,                       // SIGNUP 전용(옵션)
            String profileImageUrl             // SIGNUP 전용(옵션, pfp)
    ) {}
}
