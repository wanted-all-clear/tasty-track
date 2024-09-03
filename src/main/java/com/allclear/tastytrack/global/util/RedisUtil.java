package com.allclear.tastytrack.global.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTp;

    public RestaurantDetail getCache(int key) {

        String keyStr = changeString(key);

        RestaurantDetail value = (RestaurantDetail)redisTp.opsForValue().get(keyStr);

        if (value == null) {
            log.info("cache miss!");
        }

        return value;
    }

    public void setCache(int key, RestaurantDetail restaurantDetail) {

        String keyStr = changeString(key);

        redisTp.opsForValue().set(keyStr, restaurantDetail, 600, TimeUnit.SECONDS);
        log.info("save cache");
    }

    private String changeString(int key) {

        return String.valueOf(key);
    }

}
