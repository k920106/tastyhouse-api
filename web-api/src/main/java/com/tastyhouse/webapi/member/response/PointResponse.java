package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.point.MemberPoint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "포인트 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponse {

    @Schema(description = "사용 가능 포인트", example = "1000")
    private Integer availablePoints;

    @Schema(description = "이번달 소멸 예정 포인트", example = "0")
    private Integer expiredThisMonth;

    public static PointResponse from(MemberPoint memberPoint) {
        return PointResponse.builder()
            .availablePoints(memberPoint.getAvailablePoints())
            .expiredThisMonth(memberPoint.getExpiredThisMonth())
            .build();
    }
}
