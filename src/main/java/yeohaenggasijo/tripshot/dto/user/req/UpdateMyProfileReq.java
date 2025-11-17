package yeohaenggasijo.tripshot.dto.user.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 부분 수정(Partial Update)을 전제로 하므로
 * 필드는 모두 nullable 허용.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMyProfileReq {

    private String username;
    private String bio;
    private String avatarUrl;
    private String mobile;
    // TODO: 추후 프로필 공개 범위 등 추가 가능
}
