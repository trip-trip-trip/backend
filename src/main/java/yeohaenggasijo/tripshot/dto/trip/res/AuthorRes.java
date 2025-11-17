package yeohaenggasijo.tripshot.dto.trip.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yeohaenggasijo.tripshot.domain.user.User;

@Getter
@Builder
@AllArgsConstructor
public class AuthorRes {
    private final Long id;
    private final String username;

    @JsonProperty("avatar_url")
    private final String avatarUrl;

    public AuthorRes(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.avatarUrl = user.getAvatarUrl(); // User 엔티티에 해당 필드가 있다고 가정
    }
}
