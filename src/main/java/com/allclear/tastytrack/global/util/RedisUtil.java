package com.allclear.tastytrack.global.util;

import java.util.Set;
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

        RestaurantDetail value = (RestaurantDetail) redisTp.opsForValue().get(keyStr);

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


    // Discord 메시지 저장
    public void saveMessageToRedis(String key, Object value) {

        redisTp.opsForValue().set(key, value);
    }

    // Discord 메시지 가져오기
    public Object getMessageFromRedis(String key) {

        return redisTp.opsForValue().get(key);
    }

    // "discord:message:*(username)" 패턴의 키 키 가져오기
    public Set<String> getKeysByPattern(String pattern) {

        return redisTp.keys(pattern);
    }

    // 키로 데이터 삭제
    public void deleteKey(String key) {

        Boolean deleted = redisTp.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("키 {}에 대한 데이터가 삭제되었습니다.", key);
        } else {
            log.warn("키 {}를 삭제하지 못했습니다. 키가 존재하지 않거나 삭제 중 오류가 발생했습니다.", key);
        }
    }


}
