package com.tastyhouse.webapi.product.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ProductReviewsByRatingResponse {
    private final Map<Integer, List<ProductReviewListItem>> reviewsByRating;
    private final List<ProductReviewListItem> allReviews;
    private final Long totalReviewCount;
}
