package com.tastyhouse.core.entity.notice.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeListItemDto {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final boolean active;

    @QueryProjection
    public NoticeListItemDto(Long id, String title, String content, LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.active = active;
    }
}
