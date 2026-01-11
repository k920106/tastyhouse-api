package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "플레이스 메뉴 카테고리 응답")
public class PlaceMenuCategoryResponse {

    @Schema(description = "카테고리명", example = "대표 메뉴")
    private String categoryName;

    @Schema(description = "메뉴 목록")
    private List<PlaceMenuResponse> menus;
}
