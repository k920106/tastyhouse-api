package com.tastyhouse.webapi.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "회원 통계 응답")
public class MemberStatsResponse {

    @Schema(description = "리뷰 수", example = "7")
    private long reviewCount;

    @Schema(description = "팔로잉 수", example = "12")
    private long followingCount;

    @Schema(description = "팔로워 수", example = "34")
    private long followerCount;
}
