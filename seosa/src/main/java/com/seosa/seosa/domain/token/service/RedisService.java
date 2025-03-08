package com.seosa.seosa.domain.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 모든 Redis 데이터 삭제
    public void clearAll() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // 특정 패턴의 키만 삭제 (예: "refresh:*")
    public void clearByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}

