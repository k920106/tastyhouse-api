package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@Schema(description = "플레이스 리뷰 통계 응답")
public class PlaceReviewStatisticsResponse {

    @Schema(description = "총 평점", example = "4.8")
    private Double totalRating;

    @Schema(description = "리뷰 개수", example = "1024")
    private Long totalReviewCount;

    @Schema(description = "맛 평점", example = "3.8")
    private Double averageTasteRating;

    @Schema(description = "양 평점", example = "3.6")
    private Double averageAmountRating;

    @Schema(description = "가격 평점", example = "3.9")
    private Double averagePriceRating;

    @Schema(description = "분위기 평점", example = "3.8")
    private Double averageAtmosphereRating;

    @Schema(description = "친절 평점", example = "3.6")
    private Double averageKindnessRating;

    @Schema(description = "위생 평점", example = "3.9")
    private Double averageHygieneRating;

    @Schema(description = "재방문 의사 비율 (%)", example = "87")
    private Double willRevisitPercentage;

    @Schema(description = "월별 리뷰 수 (키: 월(1-12), 값: 리뷰 수)", example = "{\"1\": 120, \"2\": 100, \"3\": 150, \"4\": 200, \"5\": 300}")
    private Map<Integer, Long> monthlyReviewCounts;

    @Schema(description = "평점별 리뷰 수 (키: 평점(1-5), 값: 리뷰 수)", example = "{\"1\": 10, \"2\": 20, \"3\": 50, \"4\": 300, \"5\": 644}")
    private Map<Integer, Long> ratingCounts;
}
