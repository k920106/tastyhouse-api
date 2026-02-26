package com.tastyhouse.webapi.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Access Token 블랙리스트.
 * Redis TTL을 활용하여 토큰 만료 시 자동 제거되므로 별도 정리 작업이 필요 없음.
 */
@Component
@RequiredArgsConstructor
public class TokenBlacklist {

    private final TokenRedisRepository tokenRedisRepository;

    /**
     * @param token           블랙리스트에 추가할 Access Token
     * @param expirationMillis 토큰의 만료 시각 (epoch milliseconds)
     */
    public void add(String token, long expirationMillis) {
        long remainingMillis = expirationMillis - System.currentTimeMillis();
        tokenRedisRepository.addToBlacklist(token, remainingMillis);
    }

    public boolean contains(String token) {
        return tokenRedisRepository.isBlacklisted(token);
    }
}
