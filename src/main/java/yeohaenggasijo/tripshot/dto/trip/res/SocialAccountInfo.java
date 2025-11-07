package yeohaenggasijo.tripshot.dto.trip.res;

import lombok.*;

@Getter
@Builder
public class SocialAccountInfo {
    private final String socialId;
    private final String provider; // NAVER, KAKAO 등
    private final String accessToken;

    private final String name;
}
