package com.tastyhouse.webapi.review.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewDetailResponse {
    private final Long id;
    private final Long placeId;
    private final String placeName;
    private final String stationName;
    private final String content;
    private final Double totalRating;
    private final Double tasteRating;
    private final Double amountRating;
    private final Double priceRating;
    private final Double atmosphereRating;
    private final Double kindnessRating;
    private final Double hygieneRating;
    private final Boolean willRevisit;
    private final Long memberId;
    private final String memberNickname;
    private final String memberProfileImageUrl;
    private final LocalDateTime createdAt;
    private final List<String> imageUrls;
    private final List<String> tagNames;
    private final Boolean isLiked;
}
