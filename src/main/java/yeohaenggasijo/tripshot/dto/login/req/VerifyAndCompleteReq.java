package yeohaenggasijo.tripshot.dto.login.req;

import jakarta.validation.constraints.NotBlank;

public record VerifyAndCompleteReq(
        @NotBlank String phone,
        @NotBlank String code,
        // 신규 가입 케이스만 사용 (기존 유저가 있으면 무시하거나 보강 업데이트 정책에 따름)
        String username,
        String email
) {}