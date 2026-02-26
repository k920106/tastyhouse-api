package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "지도 마커 응답")
public class PlaceMapMarkerResponse {

    @Schema(description = "플레이스 ID", example = "1")
    private final Long id;

    @Schema(description = "위도", example = "37.5013")
    private final BigDecimal latitude;

    @Schema(description = "경도", example = "127.0396")
    private final BigDecimal longitude;

    @Schema(description = "상호명", example = "맛있는 집")
    private final String name;
}
