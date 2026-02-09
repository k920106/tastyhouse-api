package com.tastyhouse.webapi.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "내 리뷰 통계 응답")
public class MyReviewStatsResponse {

    @Schema(description = "전체 리뷰 개수", example = "42")
    private Integer totalReviewCount;
}
