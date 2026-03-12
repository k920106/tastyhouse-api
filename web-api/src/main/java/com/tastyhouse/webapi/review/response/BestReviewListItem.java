package com.tastyhouse.webapi.review.response;

public record BestReviewListItem(
        Long id,
        String imageUrl,
        String stationName,
        Double totalRating,
        String content
) {
}
  private final String stationName;
    private final Double totalRating;
    private final String content;
}
