package com.tastyhouse.webapi.member.request;

import com.tastyhouse.core.entity.user.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePersonalInfoRequest {

    @NotNull(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 최대 100자까지 입력 가능합니다.")
    private String fullName;

    @Pattern(regexp = "^\\d{10,11}$", message = "휴대폰번호는 10~11자리 숫자여야 합니다.")
    private String phoneNumber;

    private Integer birthDate;

    private Gender gender;

    private Boolean pushNotificationEnabled;

    private Boolean marketingInfoEnabled;

    private Boolean eventInfoEnabled;
}
