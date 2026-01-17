package com.tastyhouse.webapi.review.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "리뷰 상세 정보 (상품 정보 포함)")
public class ReviewProductResponse {

    // 상품 정보
    @Schema(description = "상품 ID", example = "1")
    private final Long productId;

    @Schema(description = "상품명", example = "아보카도 햄치즈 샌드위치")
    private final String productName;

    @Schema(description = "상품 이미지 URL", example = "https://example.com/product.jpg")
    private final String productImageUrl;

    @Schema(description = "상품 가격 (할인가가 있으면 할인가, 없으면 원가)", example = "8500")
    private final Integer productPrice;

    // 리뷰 정보
    @Schema(description = "리뷰 ID", example = "1")
    private final Long reviewId;

    @Schema(description = "리뷰 내용", example = "맛있어요!")
    private final String content;

    // 평점 정보
    @Schema(description = "총 평점", example = "3.5")
    private final Double totalRating;

    @Schema(description = "맛 평점", example = "4.0")
    private final Double tasteRating;

    @Schema(description = "양 평점", example = "3.0")
    private final Double amountRating;

    @Schema(description = "가격 평점", example = "3.0")
    private final Double priceRating;

    @Schema(description = "분위기 평점", example = "4.0")
    private final Double atmosphereRating;

    @Schema(description = "친절 평점", example = "4.0")
    private final Double kindnessRating;

    @Schema(description = "위생 평점", example = "4.0")
    private final Double hygieneRating;

    @Schema(description = "재방문 의사", example = "true")
    private final Boolean willRevisit;

    // 유저 정보
    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "회원 닉네임", example = "먹는게제일좋아")
    private final String memberNickname;

    @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String memberProfileImageUrl;

    // 작성일
    @Schema(description = "작성일", example = "2020-10-27T00:00:00")
    private final LocalDateTime createdAt;

    // 리뷰 이미지
    @Schema(description = "리뷰 이미지 URL 목록")
    private final List<String> imageUrls;

    // 태그
    @Schema(description = "태그 이름 목록", example = "[\"#샌드위치\", \"#아보카도\", \"#브런치\"]")
    private final List<String> tagNames;
}
