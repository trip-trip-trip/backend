package yeohaenggasijo.tripshot.security;

import java.util.Optional;

public interface CurrentUserProvider {
    Optional<Long> getUserId();
    default Long requireUserId() {
        return getUserId().orElseThrow(()-> new RuntimeException("User not logged in"));
    }
}
