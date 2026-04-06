package com.salon.core.infrastructure.auth;

import com.salon.core.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:token:";
    private static final Duration TTL = Duration.ofDays(7);
    private static final String NULL_VALUE = "null";

    private final StringRedisTemplate redisTemplate;

    public String issue(Long userId, UserRole role, Long storeId) {
        String token = UUID.randomUUID().toString();
        String value = userId + ":" + role.name() + ":" + (storeId != null ? storeId : NULL_VALUE);
        redisTemplate.opsForValue().set(KEY_PREFIX + token, value, TTL);
        return token;
    }

    public Optional<RefreshTokenClaims> validate(String token) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        if (value == null) {
            return Optional.empty();
        }

        String[] parts = value.split(":", 3);
        Long userId = Long.parseLong(parts[0]);
        UserRole role = UserRole.valueOf(parts[1]);
        Long storeId = NULL_VALUE.equals(parts[2]) ? null : Long.parseLong(parts[2]);

        return Optional.of(new RefreshTokenClaims(userId, role, storeId));
    }

    public void revoke(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }

    public record RefreshTokenClaims(Long userId, UserRole role, Long storeId) {}
}