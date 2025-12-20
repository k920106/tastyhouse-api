package com.tastyhouse.webapi.rank.response;

import com.tastyhouse.core.entity.rank.dto.MemberRankDto;
import com.tastyhouse.core.entity.user.MemberGrade;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyRankResponse {
    private final Long memberId;
    private final String nickname;
    private final String profileImageUrl;
    private final Integer reviewCount;
    private final Integer rankNo;
    private final MemberGrade grade;

    public static MyRankResponse from(MemberRankDto dto) {
        if (dto == null) {
            return null;
        }
        return MyRankResponse.builder()
            .memberId(dto.getMemberId())
            .nickname(dto.getNickname())
            .profileImageUrl(dto.getProfileImageUrl())
            .reviewCount(dto.getReviewCount())
            .rankNo(dto.getRankNo())
            .grade(dto.getGrade())
            .build();
    }
}
