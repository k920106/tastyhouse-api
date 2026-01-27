package com.tastyhouse.core.entity.notice.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeListItemDto {
    private final Long id;
    private final String title;
    private final LocalDateTime createdAt;

    @QueryProjection
    public NoticeListItemDto(Long id, String title, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }
}
