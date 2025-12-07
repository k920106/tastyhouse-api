package com.tastyhouse.core.entity.banner.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BannerListItemDto {
    private final Long id;
    private final String title;
    private final String imageUrl;
    private final String linkUrl;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer sort;
    private final Boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public BannerListItemDto(Long id, String title, String imageUrl, String linkUrl,
                             LocalDateTime startDate, LocalDateTime endDate,
                             Integer sort, Boolean active,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sort = sort;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}