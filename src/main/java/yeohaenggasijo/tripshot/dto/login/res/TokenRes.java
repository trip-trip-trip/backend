package yeohaenggasijo.tripshot.dto.login.res;

import yeohaenggasijo.tripshot.domain.user.User;

public record TokenRes(
        String level,
        String jwtToken,
        User user
) {
}
