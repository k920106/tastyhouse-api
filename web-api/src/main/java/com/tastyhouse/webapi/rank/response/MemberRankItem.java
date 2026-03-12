package com.tastyhouse.webapi.rank.response;

import com.tastyhouse.core.entity.user.MemberGrade;

public record MemberRankItem(
        Long memberId,
        String nickname,
        String profileImageUrl,
        Integer reviewCount,
        Integer rankNo,
        MemberGrade grade
) {
}