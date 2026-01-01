package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "플레이스 요약 정보 응답")
public class PlaceSummaryResponse {

    @Schema(description = "플레이스 ID", example = "1")
    private Long id;

    @Schema(description = "상호명", example = "리틀넥 청담")
    private String name;

    @Schema(description = "도로명 주소", example = "서울 강남구 도산대로51길 17")
    private String roadAddress;

    @Schema(description = "지번 주소", example = "서울 강남구 신사동 653-7")
    private String lotAddress;

    @Schema(description = "총 평점", example = "4.8")
    private Double rating;
}
