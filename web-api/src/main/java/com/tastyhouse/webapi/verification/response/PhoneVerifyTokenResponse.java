package com.tastyhouse.webapi.verification.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhoneVerifyTokenResponse {

    private String phoneVerifyToken;
}
