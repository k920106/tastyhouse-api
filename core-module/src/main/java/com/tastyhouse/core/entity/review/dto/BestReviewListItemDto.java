package com.tastyhouse.core.entity.review.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BestReviewListItemDto {
    private final Long id;
    private final String reviewImage;
    private final String stationName;
    private final Double totalRating;
    private final String title;
    private final String content;

    @QueryProjection
    public BestReviewListItemDto(Long id, String reviewImage, String stationName,
                                  Double totalRating, String title, String content) {
        this.id = id;
        this.reviewImage = reviewImage;
        this.stationName = stationName;
        this.totalRating = totalRating;
        this.title = title;
        this.content = content;
    }
}