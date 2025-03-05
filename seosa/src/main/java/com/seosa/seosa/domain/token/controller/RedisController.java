package com.seosa.seosa.domain.token.controller;

import com.seosa.seosa.domain.token.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @GetMapping("/keys")
    public ResponseEntity<Set<String>> getAllKeys() {
        return ResponseEntity.ok(redisService.getAllKeys());
    }

    @GetMapping("/value")
    public ResponseEntity<Map<String, String>> getValue(@RequestParam String key) {
        String value = redisService.getValue(key);
        return ResponseEntity.ok(Map.of("key", key, "value", value));
    }

    // 전체 삭제
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearAll() {
        redisService.clearAll();
        return ResponseEntity.ok(Map.of("message", "Redis 모든 데이터 삭제 완료"));
    }

    // 특정 패턴의 데이터만 삭제
    @DeleteMapping("/clear-by-pattern")
    public ResponseEntity<Map<String, String>> clearByPattern(@RequestParam String pattern) {
        redisService.clearByPattern(pattern);
        return ResponseEntity.ok(Map.of("message", "패턴 삭제 완료", "pattern", pattern));
    }
}

