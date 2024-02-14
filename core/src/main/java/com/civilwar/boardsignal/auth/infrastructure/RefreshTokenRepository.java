package com.civilwar.boardsignal.auth.infrastructure;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    public void save(String id, String refreshToken, Long refreshExpiryTime) {
        redisTemplate.opsForValue()
            .set(id, refreshToken, refreshExpiryTime, TimeUnit.MINUTES);
    }

    public Optional<String> findById(String id) {
        return Optional.ofNullable(redisTemplate.opsForValue()
            .get(id));
    }

}
