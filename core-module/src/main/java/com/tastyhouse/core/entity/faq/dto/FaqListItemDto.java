package com.tastyhouse.core.entity.faq.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FaqListItemDto {
    private final Long id;
    private final Long companyId;
    private final String title;
    private final String content;
    private final Boolean active;
    private final Integer sort;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public FaqListItemDto(Long id, Long companyId, String title, String content,
                          Boolean active, Integer sort, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.content = content;
        this.active = active;
        this.sort = sort;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
