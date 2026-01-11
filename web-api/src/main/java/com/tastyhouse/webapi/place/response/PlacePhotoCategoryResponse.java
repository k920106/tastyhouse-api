package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "플레이스 포토 카테고리 응답")
public class PlacePhotoCategoryResponse {

    @Schema(description = "카테고리명", example = "외부")
    private String categoryName;

    @Schema(description = "이미지 URL 목록")
    private List<String> imageUrls;
}
