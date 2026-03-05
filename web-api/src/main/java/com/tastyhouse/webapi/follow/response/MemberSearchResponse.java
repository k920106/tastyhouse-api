package com.tastyhouse.webapi.follow.response;

import com.tastyhouse.core.entity.user.Member;
import com.tastyhouse.core.entity.user.MemberGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberSearchResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "닉네임", example = "맛집탐험가")
    private String nickname;

    @Schema(description = "회원 등급", example = "NEWCOMER")
    private MemberGrade memberGrade;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "내가 팔로우 중인지 여부", example = "false")
    private boolean isFollowing;

    public static MemberSearchResponse of(Member member, String profileImageUrl, boolean isFollowing) {
        return MemberSearchResponse.builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .memberGrade(member.getMemberGrade())
            .profileImageUrl(profileImageUrl)
            .isFollowing(isFollowing)
            .build();
    }
}
