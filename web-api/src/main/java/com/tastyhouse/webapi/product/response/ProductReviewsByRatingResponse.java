package com.tastyhouse.webapi.product.response;

import java.util.List;
import java.util.Map;

public record ProductReviewsByRatingResponse(
        Map<Integer, List<ProductReviewListItem>> reviewsByRating,
        List<ProductReviewListItem> allReviews,
        Long totalReviewCount
) {
}
