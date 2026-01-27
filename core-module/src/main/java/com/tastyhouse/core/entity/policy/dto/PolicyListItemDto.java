package com.tastyhouse.core.entity.policy.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tastyhouse.core.entity.policy.PolicyType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PolicyListItemDto {

    private final Long id;
    private final PolicyType type;
    private final String version;
    private final String title;
    private final Boolean current;
    private final LocalDateTime effectiveDate;
    private final LocalDateTime createdAt;

    @QueryProjection
    public PolicyListItemDto(Long id, PolicyType type, String version, String title,
                           Boolean current, LocalDateTime effectiveDate, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.version = version;
        this.title = title;
        this.current = current;
        this.effectiveDate = effectiveDate;
        this.createdAt = createdAt;
    }
}
