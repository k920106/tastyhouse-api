package com.tastyhouse.webapi.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 리뷰 통계 응답")
public class ProductReviewStatisticsResponse {

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
}
