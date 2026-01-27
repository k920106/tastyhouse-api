package com.tastyhouse.webapi.policy.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PolicyUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "필수 동의 여부는 필수입니다.")
    private Boolean mandatory;

    @NotNull(message = "시행일은 필수입니다.")
    private LocalDateTime effectiveDate;

    private String updatedBy;
}
