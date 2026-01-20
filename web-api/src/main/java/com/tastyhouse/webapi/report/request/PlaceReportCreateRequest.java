package com.tastyhouse.webapi.report.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlaceReportCreateRequest(
        @NotBlank(message = "상호명은 필수입니다")
        @Size(max = 200, message = "상호명은 200자 이내로 입력해주세요")
        String businessName,

        @NotBlank(message = "주소는 필수입니다")
        @Size(max = 500, message = "주소는 500자 이내로 입력해주세요")
        String address,

        @Size(max = 500, message = "상세주소는 500자 이내로 입력해주세요")
        String addressDetail
) {
}
