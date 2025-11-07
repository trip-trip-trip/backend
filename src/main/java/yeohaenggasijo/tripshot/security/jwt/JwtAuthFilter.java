package yeohaenggasijo.tripshot.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import yeohaenggasijo.tripshot.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        var ctx = SecurityContextHolder.getContext();
        if (ctx.getAuthentication() == null) {
            String header = req.getHeader("Authorization"); // ✅ Authorization 사용
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    var p = jwtUtil.parseAndValidate(token);
                    if (p.level() == TokenLevel.ACCESS && p.userId() != null) {
                        var auth = new UsernamePasswordAuthenticationToken(
                                String.valueOf(p.userId()),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else if (p.level() == TokenLevel.SIGNUP) {
                        var principal = new SignupPrincipal(p.provider(), p.socialId(), p.phoneVerified(), p.phone());
                        var auth = new UsernamePasswordAuthenticationToken(
                                principal, null,
                                List.of(new SimpleGrantedAuthority("ROLE_SIGNUP"))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (Exception ignore) {
                    // 유효하지 않은 토큰은 인증 없이 통과 (필요시 로그)
                }
            }
        }
        chain.doFilter(req, res);
    }

}
