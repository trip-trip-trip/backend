package yeohaenggasijo.tripshot.dto.login.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OtpVerifyReq(
        @NotBlank
        @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$")
        String phone,
        @NotBlank @Size(min = 6, max = 6)
        @Pattern(regexp = "^[0-9]{6}$")
        String code
) {}