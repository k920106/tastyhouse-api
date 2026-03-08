package com.tastyhouse.webapi.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 슬라이딩 윈도우 방식으로 요청 횟수를 검증합니다.
     *
     * @param key      Redis 키 (IP, 전화번호 등)
     * @param limit    허용 최대 요청 수
     * @param duration 윈도우 기간
     * @return 제한 초과 여부 (true = 초과)
     */
    public boolean isLimitExceeded(String key, int limit, Duration duration) {
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            stringRedisTemplate.expire(key, duration);
        }
        return count != null && count > limit;
    }
}
