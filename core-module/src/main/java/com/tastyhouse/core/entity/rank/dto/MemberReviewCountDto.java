package com.tastyhouse.core.entity.rank.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberReviewCountDto {

    private final Long memberId;
    private final Long reviewCount;
    private final LocalDateTime lastReviewAt;

    @QueryProjection
    public MemberReviewCountDto(Long memberId, Long reviewCount, LocalDateTime lastReviewAt) {
        this.memberId = memberId;
        this.reviewCount = reviewCount;
        this.lastReviewAt = lastReviewAt;
    }
}