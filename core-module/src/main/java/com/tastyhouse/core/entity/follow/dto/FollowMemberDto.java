package com.tastyhouse.core.entity.follow.dto;

import com.tastyhouse.core.entity.user.MemberGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowMemberDto {
    private Long memberId;
    private String nickname;
    private MemberGrade memberGrade;
    private Long profileImageFileId;
    private boolean isFollowing;
}
