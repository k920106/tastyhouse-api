package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.user.MemberGrade;

public record MemberProfileResponse(
        Long id,
        String nickname,
        MemberGrade grade,
        String statusMessage,
        String profileImageUrl,
        String fullName,
        String phoneNumber,
        String email
) {
}
