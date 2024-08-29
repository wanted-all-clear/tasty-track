package com.allclear.tastytrack.domain.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(final RefreshToken refreshToken) {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getUsername());
        redisTemplate.expire(refreshToken.getRefreshToken(), 90L, TimeUnit.DAYS);
    }


    public Optional<RefreshToken> findByEmail(final String refreshToken) {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String userEmail = valueOperations.get(refreshToken);

        if (Objects.isNull(userEmail)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(refreshToken, userEmail));
    }

}
