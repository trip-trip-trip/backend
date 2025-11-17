package yeohaenggasijo.tripshot.dto.user.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yeohaenggasijo.tripshot.domain.user.User;

@Getter
@Builder
@AllArgsConstructor
public class MyProfileRes {

    private final Long id;
    private final String username;
    private final String tag;
    private final String bio;
    private final String avatarUrl;
    private final String mobile;

    public static MyProfileRes from(User user) {
        if (user == null) {
            return null;
        }
        return MyProfileRes.builder()
                .id(user.getId())
                .username(user.getUsername())
                .tag(user.getTag())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .mobile(user.getMobile())
                .build();
    }
}
