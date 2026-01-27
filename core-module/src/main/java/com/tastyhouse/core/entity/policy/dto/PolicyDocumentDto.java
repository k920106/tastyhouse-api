package com.tastyhouse.core.entity.policy.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tastyhouse.core.entity.policy.PolicyType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PolicyDocumentDto {

    private final Long id;
    private final PolicyType type;
    private final String version;
    private final String title;
    private final String content;
    private final Boolean current;
    private final Boolean mandatory;
    private final LocalDateTime effectiveDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public PolicyDocumentDto(Long id, PolicyType type, String version, String title,
                           String content, Boolean current, Boolean mandatory,
                           LocalDateTime effectiveDate, LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.version = version;
        this.title = title;
        this.content = content;
        this.current = current;
        this.mandatory = mandatory;
        this.effectiveDate = effectiveDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
