package com.tastyhouse.core.entity.banner.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BannerListItemDto {
    private final Long id;
    private final String title;
    private final String imageUrl;
    private final String linkUrl;

    @QueryProjection
    public BannerListItemDto(Long id, String title, String imageUrl, String linkUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
    }
}