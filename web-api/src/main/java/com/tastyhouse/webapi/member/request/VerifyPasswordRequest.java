package com.tastyhouse.webapi.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 인증 요청")
public class VerifyPasswordRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "현재 비밀번호", example = "myPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
