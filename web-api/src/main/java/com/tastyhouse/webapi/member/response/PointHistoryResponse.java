package com.tastyhouse.webapi.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "포인트 내역 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {

    @Schema(description = "사용 가능 포인트", example = "1000")
    private Integer availablePoints;

    @Schema(description = "이번달 소멸 예정 포인트", example = "0")
    private Integer expiredThisMonth;

    @Schema(description = "포인트 내역 목록")
    private List<PointHistoryItemResponse> histories;
}
