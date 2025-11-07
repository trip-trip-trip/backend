package yeohaenggasijo.tripshot.security.jwt;

import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;

import java.security.Principal;

public record SignupPrincipal(
        SocialLoginType provider,
        String socialId,
        boolean phoneVerified,
        String phone // 검증 후 세팅
) implements Principal {
    @Override public String getName() {
        return provider + ":" + socialId;
    }
}