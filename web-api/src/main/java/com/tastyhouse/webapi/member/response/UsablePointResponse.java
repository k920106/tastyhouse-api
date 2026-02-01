package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.point.MemberPoint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용 가능 포인트 응답 DTO (주문용)")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsablePointResponse {

    @Schema(description = "사용 가능 포인트", example = "1000")
    private Integer usablePoints;

    public static UsablePointResponse from(MemberPoint memberPoint) {
        return UsablePointResponse.builder()
            .usablePoints(memberPoint.getAvailablePoints())
            .build();
    }
}
