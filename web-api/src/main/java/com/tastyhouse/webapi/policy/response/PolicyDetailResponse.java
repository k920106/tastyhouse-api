package com.tastyhouse.webapi.policy.response;

import com.tastyhouse.core.entity.policy.PolicyType;

import java.time.LocalDateTime;

public record PolicyDetailResponse(
        Long id,
        PolicyType type,
        String version,
        String title,
        String content,
        Boolean current,
        Boolean mandatory,
        LocalDateTime effectiveDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
