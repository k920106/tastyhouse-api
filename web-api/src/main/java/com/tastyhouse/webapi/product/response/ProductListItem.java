package com.tastyhouse.webapi.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "상품 목록 아이템")
public class ProductListItem {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "명란 크림 파스타")
    private String name;

    @Schema(description = "상품 설명", example = "신선한 명란과 크림소스의 조화")
    private String description;

    @Schema(description = "이미지 URL", example = "https://example.com/product.jpg")
    private String imageUrl;

    @Schema(description = "원가", example = "18500")
    private Integer originalPrice;

    @Schema(description = "할인가", example = "16650")
    private Integer discountPrice;

    @Schema(description = "할인율", example = "10.00")
    private BigDecimal discountRate;

    @Schema(description = "상품 평점", example = "4.5")
    private Double rating;

    @Schema(description = "리뷰 수", example = "128")
    private Integer reviewCount;

    @Schema(description = "대표 메뉴 여부", example = "true")
    private Boolean isRepresentative;

    @Schema(description = "품절 여부", example = "false")
    private Boolean isSoldOut;

    @Schema(description = "카테고리명", example = "시그니처 메뉴")
    private String categoryName;
}
