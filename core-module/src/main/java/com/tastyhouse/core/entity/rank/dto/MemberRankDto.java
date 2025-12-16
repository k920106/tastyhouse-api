package com.tastyhouse.core.entity.rank.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tastyhouse.core.entity.user.MemberGrade;
import lombok.Getter;

@Getter
public class MemberRankDto {

    private final Long memberId;
    private final String nickname;
    private final String profileImageUrl;
    private final Integer reviewCount;
    private final Integer rankNo;
    private final MemberGrade grade;

    @QueryProjection
    public MemberRankDto(
        Long memberId,
        String nickname,
        String profileImageUrl,
        Integer reviewCount,
        Integer rankNo,
        MemberGrade grade
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.reviewCount = reviewCount;
        this.rankNo = rankNo;
        this.grade = grade;
    }
}