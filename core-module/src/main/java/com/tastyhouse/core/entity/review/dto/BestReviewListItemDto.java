package com.tastyhouse.core.entity.review.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BestReviewListItemDto {
    private final Long id;
    private final String imageUrl;
    private final String stationName;
    private final Double totalRating;
    private final String content;

    @QueryProjection
    public BestReviewListItemDto(Long id, String imageUrl, String stationName,
                                 Double totalRating, String content) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.stationName = stationName;
        this.totalRating = totalRating;
        this.content = content;
    }
}
