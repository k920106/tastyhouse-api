package com.tastyhouse.webapi.report.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PlaceReportResponse(
        Long id,
        String businessName,
        String address,
        String addressDetail,
        LocalDateTime createdAt
) {
}
