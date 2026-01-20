package com.tastyhouse.webapi.partnership.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PartnershipRequestCreateRequest(
    @NotBlank(message = "상호명은 필수입니다")
    @Size(max = 200, message = "상호명은 200자 이내로 입력해주세요")
    String businessName,

    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 500, message = "주소는 500자 이내로 입력해주세요")
    String address,

    @Size(max = 500, message = "상세주소는 500자 이내로 입력해주세요")
    String addressDetail,

    @NotBlank(message = "성명은 필수입니다")
    @Size(max = 100, message = "성명은 100자 이내로 입력해주세요")
    String contactName,

    @NotBlank(message = "연락처는 필수입니다")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    String contactPhone,

    @NotNull(message = "상담신청시간은 필수입니다")
    LocalDateTime consultationRequestedAt
) {
}
