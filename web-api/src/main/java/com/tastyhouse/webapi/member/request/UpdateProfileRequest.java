package com.tastyhouse.webapi.member.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 50, message = "닉네임은 최대 50자까지 입력 가능합니다.")
    private String nickname;

    @Size(max = 200, message = "상태메시지는 최대 200자까지 입력 가능합니다.")
    private String statusMessage;

    @Size(max = 500, message = "프로필 이미지 URL은 최대 500자까지 입력 가능합니다.")
    private String profileImageUrl;
}
