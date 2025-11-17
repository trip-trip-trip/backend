package yeohaenggasijo.tripshot.dto.login.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpSendReq(
        @NotBlank
        @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "E.164 국제전화 형식이어야 합니다.")
        String phone
) {
}
