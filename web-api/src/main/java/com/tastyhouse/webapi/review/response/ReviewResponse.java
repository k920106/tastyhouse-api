package com.tastyhouse.webapi.review.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "리뷰 등록/수정 응답")
public class ReviewResponse {

    @Schema(description = "리뷰 ID", example = "1")
    private Long reviewId;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "맛 평점", example = "4.0")
    private Double tasteRating;

    @Schema(description = "양 평점", example = "3.0")
    private Double amountRating;

    @Schema(description = "가격 평점", example = "3.0")
    private Double priceRating;

    @Schema(description = "총 평점", example = "3.3")
    private Double totalRating;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 이미지 URL 목록")
    private List<String> imageUrls;

    @Schema(description = "태그 목록")
    private List<String> tags;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;
}
