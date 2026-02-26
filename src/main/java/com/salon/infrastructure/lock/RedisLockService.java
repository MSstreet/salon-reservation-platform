package com.salon.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "  return redis.call('del', KEYS[1]) " +
            "else " +
            "  return 0 " +
            "end";

    public String tryLock(String key, long waitTimeMs, long leaseTimeMs) {
        String ownerValue = UUID.randomUUID().toString();
        long deadline = System.currentTimeMillis() + waitTimeMs;

        while (System.currentTimeMillis() < deadline) {
            Boolean acquired = redisTemplate.opsForValue()
                    .setIfAbsent(key, ownerValue, Duration.ofMillis(leaseTimeMs));

            if (Boolean.TRUE.equals(acquired)) {
                log.debug("Lock acquired: key={}, owner={}", key, ownerValue);
                return ownerValue;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        log.warn("Lock acquisition failed: key={}, waitTimeMs={}", key, waitTimeMs);
        return null;
    }

    public void unlock(String key, String ownerValue) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script, List.of(key), ownerValue);

        if (result != null && result == 1L) {
            log.debug("Lock released: key={}, owner={}", key, ownerValue);
        } else {
            log.warn("Lock release failed (not owner or expired): key={}, owner={}", key, ownerValue);
        }
    }
}
