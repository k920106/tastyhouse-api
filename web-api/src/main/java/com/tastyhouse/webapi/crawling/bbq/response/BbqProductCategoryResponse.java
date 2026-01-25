package com.tastyhouse.webapi.crawling.bbq.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * BBQ 상품 카테고리 응답 DTO
 *
 * ProductCategory Entity 구조에 맞춘 응답입니다.
 */
@Getter
@Builder
@Schema(description = "BBQ 상품 카테고리 응답")
public class BbqProductCategoryResponse {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "플레이스 ID", example = "1")
    private Long placeId;

    @Schema(description = "카테고리명", example = "시그니처 메뉴")
    private String name;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sort;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;
}
