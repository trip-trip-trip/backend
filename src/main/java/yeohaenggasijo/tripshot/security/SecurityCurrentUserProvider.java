//package yeohaenggasijo.tripshot.security;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class SecurityCurrentUserProvider implements CurrentUserProvider {
//    @Override
//    public Optional<Long> getUserId(){
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
//        try { return Optional.of(Long.parseLong(auth.getName())); }
//        catch (NumberFormatException e) { return Optional.empty(); }
//    }
//}
