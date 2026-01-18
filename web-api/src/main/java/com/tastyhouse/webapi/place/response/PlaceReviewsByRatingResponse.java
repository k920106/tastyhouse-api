package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class PlaceReviewsByRatingResponse {
    private final Map<Integer, List<PlaceReviewListItem>> reviewsByRating;
    private final List<PlaceReviewListItem> allReviews;
    private final Long totalReviewCount;
}
