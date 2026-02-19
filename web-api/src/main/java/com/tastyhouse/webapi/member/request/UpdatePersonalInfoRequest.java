package com.tastyhouse.webapi.member.request;

import com.tastyhouse.core.entity.user.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "개인정보 수정 요청")
public class UpdatePersonalInfoRequest {

    @NotNull(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 최대 100자까지 입력 가능합니다.")
    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Pattern(regexp = "^\\d{10,11}$", message = "휴대폰번호는 10~11자리 숫자여야 합니다.")
    @Schema(description = "휴대폰번호 (10~11자리 숫자)", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "생년월일 (YYYYMMDD)", example = "19900101")
    private Integer birthDate;

    @Schema(description = "성별 (MALE / FEMALE)", example = "MALE")
    private Gender gender;

    @Schema(description = "푸시 알림 수신 동의", example = "true")
    private Boolean pushNotificationEnabled;

    @Schema(description = "마케팅 정보 수신 동의", example = "false")
    private Boolean marketingInfoEnabled;

    @Schema(description = "이벤트 정보 수신 동의", example = "true")
    private Boolean eventInfoEnabled;
}
