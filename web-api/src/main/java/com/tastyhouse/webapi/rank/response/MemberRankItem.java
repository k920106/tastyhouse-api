package com.tastyhouse.webapi.rank.response;

import com.tastyhouse.core.entity.user.MemberGrade;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberRankItem {

    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private Integer reviewCount;
    private Integer rankNo;
    private MemberGrade grade;
}