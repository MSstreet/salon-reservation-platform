package com.salon.core.infrastructure.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "idempotency:reservation:";
    private static final Duration TTL = Duration.ofHours(24);

    public Optional<Long> getReservationId(String idempotencyKey) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + idempotencyKey);
        return Optional.ofNullable(value).map(Long::parseLong);
    }

    public void save(String idempotencyKey, Long reservationId) {
        redisTemplate.opsForValue().set(KEY_PREFIX + idempotencyKey, String.valueOf(reservationId), TTL);
    }
}