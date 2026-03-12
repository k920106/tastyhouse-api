package com.tastyhouse.webapi.auth.response;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
