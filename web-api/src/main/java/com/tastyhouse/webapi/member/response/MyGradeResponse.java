package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.user.MemberGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 등급 정보 응답")
public class MyGradeResponse {

    @Schema(description = "현재 등급 코드", example = "INSIDER")
    private final MemberGrade currentGrade;

    @Schema(description = "현재 등급 이름", example = "인싸멤버")
    private final String currentGradeDisplayName;

    @Schema(description = "다음 등급 코드 (최고 등급이면 null)", example = "GOURMET")
    private final MemberGrade nextGrade;

    @Schema(description = "다음 등급 이름 (최고 등급이면 null)", example = "미식멤버")
    private final String nextGradeDisplayName;

    @Schema(description = "현재 작성 리뷰 수", example = "625")
    private final int currentReviewCount;

    @Schema(description = "다음 등급까지 필요한 리뷰 개수 (최고 등급이면 0)", example = "75")
    private final int reviewsNeededForNextGrade;
}
