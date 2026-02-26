package com.tastyhouse.core.entity.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class PlaceReviewStatisticsDto {
    private final Long totalReviewCount;
    private final Double averageTasteRating;
    private final Double averageAmountRating;
    private final Double averagePriceRating;
    private final Double averageAtmosphereRating;
    private final Double averageKindnessRating;
    private final Double averageHygieneRating;
    private final Double willRevisitPercentage;
    private final Map<Integer, Long> ratingCounts;
    private final Map<Integer, Long> monthlyReviewCounts;
}
