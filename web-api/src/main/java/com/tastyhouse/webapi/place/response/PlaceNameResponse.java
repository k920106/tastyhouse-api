package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "플레이스 이름 조회 응답")
public class PlaceNameResponse {

    @Schema(description = "플레이스 ID", example = "1")
    private Long id;

    @Schema(description = "상호명", example = "리틀넥 청담")
    private String name;
}