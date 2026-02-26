package com.tastyhouse.webapi.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 토큰 저장소
 *
 * - Refresh Token: "rt:{username}" → refreshToken 값 (TTL: refreshTokenExpiration)
 * - Access Token Blacklist: "bl:{accessToken}" → "logout" (TTL: 남은 만료 시간)
 */
@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {

    private static final String REFRESH_TOKEN_PREFIX = "rt:";
    private static final String BLACKLIST_PREFIX = "bl:";
    private static final String BLACKLISTED_VALUE = "logout";

    private final StringRedisTemplate redisTemplate;

    // ──────────────────────────────────────────────────────
    // Refresh Token
    // ──────────────────────────────────────────────────────

    public void saveRefreshToken(String username, String refreshToken, long ttlMillis) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + username,
                refreshToken,
                ttlMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + username);
    }

    public boolean isRefreshTokenValid(String username, String refreshToken) {
        String stored = getRefreshToken(username);
        return refreshToken.equals(stored);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + username);
    }

    // ──────────────────────────────────────────────────────
    // Access Token Blacklist
    // ──────────────────────────────────────────────────────

    /**
     * 로그아웃된 Access Token을 블랙리스트에 등록.
     * TTL은 토큰 만료 시간까지만 유지되어 메모리 낭비 없음.
     *
     * @param accessToken    블랙리스트에 추가할 토큰
     * @param remainingMillis 토큰 만료까지 남은 시간 (ms)
     */
    public void addToBlacklist(String accessToken, long remainingMillis) {
        if (remainingMillis > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    BLACKLISTED_VALUE,
                    remainingMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken));
    }
}
