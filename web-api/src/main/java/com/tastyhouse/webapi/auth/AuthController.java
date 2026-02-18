package com.tastyhouse.webapi.auth;

import com.tastyhouse.webapi.auth.request.LoginRequest;
import com.tastyhouse.webapi.auth.request.RefreshTokenRequest;
import com.tastyhouse.webapi.auth.response.JwtResponse;
import com.tastyhouse.webapi.config.jwt.JwtTokenProvider;
import com.tastyhouse.webapi.config.jwt.TokenBlacklist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;

    @Operation(summary = "로그인", description = "사용자 인증을 통해 JWT 토큰을 발급합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = JwtResponse.class))), @ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치)", content = @Content(schema = @Schema(hidden = true)))})
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그인 요청 정보", required = true, content = @Content(schema = @Schema(implementation = LoginRequest.class))) @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, "Bearer"));
    }

    @Operation(summary = "로그아웃", description = "Access Token을 블랙리스트에 등록하여 로그아웃 처리합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(hidden = true))), @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰", content = @Content(schema = @Schema(hidden = true)))})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        String accessToken = bearerToken.substring(7).trim();
        if (jwtTokenProvider.validateToken(accessToken)) {
            long expirationMillis = jwtTokenProvider.getExpirationMillis(accessToken);
            tokenBlacklist.add(accessToken, expirationMillis);
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "토큰 갱신 성공", content = @Content(schema = @Schema(implementation = JwtResponse.class))), @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token", content = @Content(schema = @Schema(hidden = true)))})
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Refresh Token", required = true, content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))) @RequestBody RefreshTokenRequest request) {

        String newAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(request.getRefreshToken());
        String newRefreshToken = jwtTokenProvider.createRefreshTokenFromRefreshToken(request.getRefreshToken());

        return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken, "Bearer"));
    }
}
