package com.tastyhouse.webapi.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoResponse {
    private Long id;
    private String profileImageUrl;
}
