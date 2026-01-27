package com.tastyhouse.webapi.policy.response;

import com.tastyhouse.core.entity.policy.PolicyType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PolicyListItemResponse {

    private Long id;
    private PolicyType type;
    private String version;
    private String title;
    private Boolean current;
    private LocalDateTime effectiveDate;
    private LocalDateTime createdAt;
}
