package com.tastyhouse.webapi.review.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BestReviewListItem {
    private final Long id;
    private final String imageUrl;
    private final String stationName;
    private final Double totalRating;
    private final String title;
    private final String content;
}
