package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.point.MemberPointHistory;
import com.tastyhouse.core.entity.point.PointType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "포인트 내역 항목")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryItemResponse {

    @Schema(description = "포인트 사용/적립 사유", example = "포토 리뷰 적립금")
    private String reason;

    @Schema(description = "내역 일자", example = "2020-10-05")
    private LocalDate date;

    @Schema(description = "포인트 변동량 (적립 시 양수, 사용 시 음수)", example = "1000")
    private Integer pointAmount;

    @Schema(description = "포인트 유형 (EARNED: 적립, USE: 사용, REFUND: 환불)", example = "EARNED")
    private PointType pointType;

    public static PointHistoryItemResponse from(MemberPointHistory history) {
        return PointHistoryItemResponse.builder()
            .reason(history.getReason())
            .date(history.getCreatedAt().toLocalDate())
            .pointAmount(history.getPointType() == PointType.USE
                ? -history.getPointAmount()
                : history.getPointAmount())
            .pointType(history.getPointType())
            .build();
    }
}
