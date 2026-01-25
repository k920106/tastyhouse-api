package com.tastyhouse.webapi.crawling.bbq.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * BBQ 상품 응답 DTO
 *
 * Product Entity 구조에 맞춘 응답입니다.
 */
@Getter
@Builder
@Schema(description = "BBQ 상품 응답")
public class BbqProductResponse {

    @Schema(description = "상품 ID", example = "30261")
    private Long id;

    @Schema(description = "상품명", example = "황금올리브치킨™반마리+BBQ 떡볶이+수제맥주 2잔")
    private String name;

    @Schema(description = "상품 설명", example = "겉은 바삭 육즙 가득한 부드러운 속살이 환상적인 건강한 치킨")
    private String description;

    @Schema(description = "이미지 URL", example = "https://static.bbqorder.co.kr/menu/...")
    private String imageUrl;

    @Schema(description = "원가", example = "27500")
    private Integer originalPrice;

    @Schema(description = "추가 가격", example = "1000")
    private Integer addPrice;

    @Schema(description = "품절 여부", example = "false")
    private Boolean isSoldOut;

    @Schema(description = "성인 전용 여부", example = "true")
    private Boolean isAdultOnly;

    @Schema(description = "배달 가능 여부", example = "true")
    private Boolean canDeliver;

    @Schema(description = "포장 가능 여부", example = "true")
    private Boolean canTakeout;
}
