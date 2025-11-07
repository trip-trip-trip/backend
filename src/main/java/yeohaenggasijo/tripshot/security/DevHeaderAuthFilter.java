package yeohaenggasijo.tripshot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Profile("dev")
@Component
public class DevHeaderAuthFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String ROLES_HEADER   = "X-User-Roles"; // 쉼표구분: "USER,ADMIN"

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        var context = SecurityContextHolder.getContext();
        var current = context.getAuthentication();

        boolean hasAuth = current != null && !(current instanceof AnonymousAuthenticationToken);
        if (!hasAuth) {
            String uid = req.getHeader(USER_ID_HEADER);

            if (uid != null && !uid.isBlank()) {
                // 기본 역할 ROLE_USER, 추가 역할은 X-User-Roles 로 주입 가능
                List<SimpleGrantedAuthority> authorities =
                        parseRoles(req.getHeader(ROLES_HEADER));

                var token = new UsernamePasswordAuthenticationToken(
                        uid, null, authorities
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }

        chain.doFilter(req, res);
    }

    private List<SimpleGrantedAuthority> parseRoles(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}