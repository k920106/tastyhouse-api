package com.tastyhouse.webapi.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberContactResponse {
    private String fullName;
    private String phoneNumber;
    private String email;
}
