package com.tastyhouse.webapi.policy.response;

import com.tastyhouse.core.entity.policy.PolicyType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PolicyDetailResponse {

    private Long id;
    private PolicyType type;
    private String version;
    private String title;
    private String content;
    private Boolean current;
    private Boolean mandatory;
    private LocalDateTime effectiveDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
