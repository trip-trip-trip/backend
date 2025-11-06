//package yeohaenggasijo.tripshot.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.boot.autoconfigure.security.SecurityProperties;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Profile({"dev", "test"})
//@Component
//@Order(SecurityProperties.BASIC_AUTH_ORDER - 1)
//public class DevHeaderAuthFilter extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//            throws ServletException, IOException {
//        if (SecurityContextHolder.getContext().getAuthentication() == null) {
//            String uid = req.getHeader("X-User-Id");
//            if (uid != null && !uid.isBlank()) {
//                var auth = new UsernamePasswordAuthenticationToken(uid, "N/A",
//                        List.of(new SimpleGrantedAuthority("ROLE_USER")));
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//    }
//
//}
