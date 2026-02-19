package com.tastyhouse.webapi.place.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "근처 플레이스 조회 요청")
public record PlaceNearRequest(
    @Schema(description = "위도", example = "37.5013")
    Double latitude,

    @Schema(description = "경도", example = "127.0396")
    Double longitude
) {
}
