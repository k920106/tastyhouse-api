package com.tastyhouse.core.entity.review.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MyReviewListItemDto {
    private final Long id;
    private final String imageUrl;

    @QueryProjection
    public MyReviewListItemDto(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
