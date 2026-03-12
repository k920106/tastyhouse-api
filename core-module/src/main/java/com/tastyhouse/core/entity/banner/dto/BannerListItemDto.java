package com.tastyhouse.core.entity.banner.dto;

import com.querydsl.core.annotations.QueryProjection;

public record BannerListItemDto(
        Long id,
        String title,
        String imageUrl,
        String linkUrl
) {
    @QueryProjection
    public BannerListItemDto {
    }
}
