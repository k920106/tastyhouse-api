package com.tastyhouse.webapi.report.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BugReportCreateRequest(
    @NotBlank(message = "단말기 정보는 필수입니다")
    @Size(max = 100, message = "단말기 정보는 100자 이내로 입력해주세요")
    String device,

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요")
    String title,

    @NotBlank(message = "내용은 필수입니다")
    String content,

    List<String> imageUrls
) {
}
