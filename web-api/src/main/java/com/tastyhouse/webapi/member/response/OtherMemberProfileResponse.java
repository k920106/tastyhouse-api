package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.user.MemberGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OtherMemberProfileResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "닉네임", example = "맛집탐험가")
    private String nickname;

    @Schema(description = "회원 등급", example = "NEWCOMER")
    private MemberGrade memberGrade;

    @Schema(description = "상태 메시지", example = "오늘도 맛있는 하루!")
    private String statusMessage;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "내가 이 회원을 팔로우 중인지 여부", example = "false")
    private boolean isFollowing;
}
