package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.user.MemberGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberProfileResponse {
    private Long id;
    private String nickname;
    private MemberGrade grade;
    private Integer reviewCount;
    private String statusMessage;
    private String profileImageUrl;
}
