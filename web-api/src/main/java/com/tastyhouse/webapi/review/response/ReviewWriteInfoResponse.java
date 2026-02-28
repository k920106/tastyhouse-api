package com.tastyhouse.webapi.review.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 작성 페이지 정보 응답")
public class ReviewWriteInfoResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "아보카도 햄치즈 샌드위치")
    private String productName;

    @Schema(description = "상품 이미지 URL")
    private String productImageUrl;

    @Schema(description = "상품 가격", example = "8500")
    private Integer productPrice;

    @Schema(description = "주문 ID (주문 기반 리뷰인 경우)", example = "100")
    private Long orderId;

    @Schema(description = "이미 리뷰를 작성했는지 여부", example = "false")
    private Boolean isReviewed;
}
