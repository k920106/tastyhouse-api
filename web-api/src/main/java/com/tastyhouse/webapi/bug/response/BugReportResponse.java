package com.tastyhouse.webapi.bug.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BugReportResponse(
    Long id,
    String device,
    String title,
    String content,
    List<Long> uploadedFileIds,
    LocalDateTime createdAt
) {
}
