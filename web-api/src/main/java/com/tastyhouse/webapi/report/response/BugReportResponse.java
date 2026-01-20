package com.tastyhouse.webapi.report.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BugReportResponse(
    Long id,
    String device,
    String title,
    String content,
    List<String> imageUrls,
    LocalDateTime createdAt
) {
}
