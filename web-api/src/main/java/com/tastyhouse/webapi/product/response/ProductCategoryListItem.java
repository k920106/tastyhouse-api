package com.tastyhouse.webapi.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 카테고리 목록 아이템")
public class ProductCategoryListItem {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 표시명", example = "시그니처 메뉴")
    private String displayName;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sort;
}
