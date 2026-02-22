package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.user.Gender;
import com.tastyhouse.core.entity.user.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "개인정보 조회 응답")
public class PersonalInfoResponse {

    @Schema(description = "아이디 (이메일)", example = "kimcs1234@naver.com")
    private String email;

    @Schema(description = "이름", example = "김철수")
    private String fullName;

    @Schema(description = "휴대폰번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "생년월일 (YYYYMMDD)", example = "20200717")
    private Integer birthDate;

    @Schema(description = "성별 (MALE / FEMALE)", example = "FEMALE")
    private Gender gender;

    @Schema(description = "푸시 알림 수신 동의", example = "false")
    private Boolean pushNotificationEnabled;

    @Schema(description = "마케팅 정보 수신 동의", example = "false")
    private Boolean marketingInfoEnabled;

    @Schema(description = "이벤트 정보 수신 동의", example = "false")
    private Boolean eventInfoEnabled;

    public static PersonalInfoResponse from(Member member) {
        return PersonalInfoResponse.builder()
                .email(member.getUsername())
                .fullName(member.getFullName())
                .phoneNumber(member.getPhoneNumber())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .pushNotificationEnabled(member.getPushNotificationEnabled())
                .marketingInfoEnabled(member.getMarketingInfoEnabled())
                .eventInfoEnabled(member.getEventInfoEnabled())
                .build();
    }
}
