package com.tastyhouse.webapi.report.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "맛집 제보 요청")
public record PlaceReportCreateRequest(
    @NotBlank(message = "상호명은 필수입니다")
    @Size(max = 200, message = "상호명은 200자 이내로 입력해주세요")
    @Schema(description = "상호명", example = "맛집식당", requiredMode = Schema.RequiredMode.REQUIRED)
    String businessName,

    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 500, message = "주소는 500자 이내로 입력해주세요")
    @Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.REQUIRED)
    String address,

    @Size(max = 500, message = "상세주소는 500자 이내로 입력해주세요")
    @Schema(description = "상세 주소", example = "2층 201호")
    String addressDetail
) {
}
