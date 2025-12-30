package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "플레이스 메뉴 응답")
public class PlaceMenuResponse {

    @Schema(description = "메뉴 ID", example = "1")
    private Long id;

    @Schema(description = "메뉴명", example = "명란 크림 파스타")
    private String name;

    @Schema(description = "이미지 URL", example = "https://example.com/menu.jpg")
    private String imageUrl;

    @Schema(description = "원가", example = "18500")
    private Integer originalPrice;

    @Schema(description = "할인가", example = "18000")
    private Integer discountPrice;

    @Schema(description = "할인율", example = "10")
    private BigDecimal discountRate;

    @Schema(description = "메뉴 평점", example = "3.5")
    private Double rating;

    @Schema(description = "리뷰 수", example = "24")
    private Integer reviewCount;

    @Schema(description = "대표 메뉴 여부", example = "true")
    private Boolean isRepresentative;
}
