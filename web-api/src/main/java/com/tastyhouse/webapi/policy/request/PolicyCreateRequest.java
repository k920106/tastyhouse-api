package com.tastyhouse.webapi.policy.request;

import com.tastyhouse.core.entity.policy.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PolicyCreateRequest {

    @NotNull(message = "정책 타입은 필수입니다.")
    private PolicyType type;

    @NotBlank(message = "버전은 필수입니다.")
    private String version;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "필수 동의 여부는 필수입니다.")
    private Boolean mandatory;

    @NotNull(message = "시행일은 필수입니다.")
    private LocalDateTime effectiveDate;

    private String createdBy;
}
