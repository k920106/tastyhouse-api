package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "플레이스 사진 응답")
public class PlacePhotoResponse {

    @Schema(description = "이미지 ID", example = "1")
    private Long id;

    @Schema(description = "이미지 URL", example = "https://example.com/photo.jpg")
    private String imageUrl;

    @Schema(description = "이미지 카테고리 코드", example = "EXTERIOR")
    private String categoryCode;

    @Schema(description = "이미지 카테고리명", example = "가게 외관")
    private String categoryName;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sort;
}
