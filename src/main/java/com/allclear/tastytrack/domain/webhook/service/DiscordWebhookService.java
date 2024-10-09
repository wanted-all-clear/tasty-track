package com.allclear.tastytrack.domain.webhook.service;

import com.allclear.tastytrack.domain.region.entity.Region;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.user.dto.UserInfo;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import com.allclear.tastytrack.domain.user.service.UserService;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import com.allclear.tastytrack.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscordWebhookService {

    // Discord 웹훅 URL
    @Value("${DISCORD_WEBHOOK_URL}")
    private String webhookUrl;

    private final RedisUtil redisUtil;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * 매주 월-금 오전 11시 20분에 실행되는 스케줄러.
     * Redis에서 미리 저장된 각 사용자의 메시지를 가져와 디스코드로 전송하고,
     * 성공적으로 전송된 메시지는 Redis에서 삭제합니다.
     * 작성자: 배서진
     */
    @Scheduled(cron = "0 20 11 * * MON-FRI")
    public void sendScheduledMessage() {

        // Redis에서 사용자별 메시지 키 목록 가져오기
        Set<String> keys = redisUtil.getKeysByPattern("discord:message:*");
        if (keys.isEmpty()) {
            log.info("스케줄러: Redis에 저장된 Discord 메시지가 없습니다.");
        }

        // 각 키에 대해 메시지를 가져와 Discord에 전송
        for (String key : keys) {
            Object jsonBody = redisUtil.getMessageFromRedis(key);
            if (jsonBody != null) {
                log.info("스케줄러: Redis에서 메시지를 가져와 Discord에 전송합니다. 키: {}", key);
                try {
                    sendMessage(jsonBody.toString());

                    // 메시지 전송 성공 시 Redis에서 키 삭제
                    redisUtil.deleteKey(key);
                    log.info("스케줄러: 키 '{}'에 대한 메시지를 성공적으로 전송 후 Redis에서 삭제했습니다.", key);
                } catch (CustomException e) {
                    log.error("스케줄러: 메시지 전송 실패, Redis에서 키 '{}'에 대한 메시지를 유지합니다.", key);
                }
            } else {
                log.warn("스케줄러: Redis에서 키 '{}'에 대한 메시지가 없습니다.", key);
            }
        }

    }

    /**
     * 매주 월-금 오전 11시에 실행되는 스케줄러.
     * 점심 추천이 활성화된 사용자들에 한해, 디스코드 메시지를 생성하여 Redis에 저장합니다.
     * 작성자: 배서진
     */
    @Scheduled(cron = "0 00 11 * * MON-FRI")
    public void saveDiscordMessage() {

        List<User> userList = userRepository.findAll();

        // 점심 추천이 활성화된 사용자 필터링
        List<User> activeUsers = userList.stream()
                .filter(User::isLunchRecommendYn)  // 점심 추천이 활성화된 사용자만 필터링
                .collect(Collectors.toList());

        if (activeUsers.isEmpty()) {
            log.info("점심 추천이 활성화된 사용자가 없습니다.");
        }

        // 점심 추천이 활성화된 각 사용자에 대해 메시지를 준비
        for (User user : activeUsers) {
            try {
                // UserInfo를 가져옴
                UserInfo userInfo = userService.getUserInfo(user.getUsername());

                // 메시지 생성 및 Redis에 저장
                String message = generateMessage(userInfo);
                String redisKey = "discord:message:" + userInfo.getUsername();
                redisUtil.saveMessageToRedis(redisKey, message);

                log.info("사용자 {}의 메시지를 미리 준비하여 Redis에 저장했습니다.", userInfo.getUsername());

            } catch (Exception e) {
                log.error("사용자 {}에 대한 메시지 준비 중 오류 발생: {}", user.getUsername(), e.getMessage());
            }
        }
    }

    /**
     * 특정 사용자(UserInfo)에게 Discord로 전송할 메시지를 생성합니다.
     * 사용자 위치와 선호 음식을 기반으로 추천 맛집 목록을 생성하고, 이를 JSON 형식의 메시지로 변환합니다.
     * 작성자: 배서진
     */
    private String generateMessage(UserInfo userInfo) {

        double distance = 5.0; // 5km 범위 설정
        Map<String, String> restaurantMessages = new LinkedHashMap<>();
        List<String> categories = List.of("한식", "일식", "중국식", "호프/통닭", "기타");

        // 각 음식 종류별로 레스토랑 추천 리스트 생성
        for (String type : categories) {
            List<String> restaurants = restaurantRepository.findTop5ByTypeAndDistance(
                            type, userInfo.getLon(), userInfo.getLat(), distance)
                    .stream()
                    .map(restaurant -> {
                        try {
                            String restaurantName = URLEncoder.encode(restaurant.getName(), "UTF-8");
                            return "[" + restaurant.getName() + "](https://map.naver.com/p/search/" + restaurantName + ")";
                        } catch (UnsupportedEncodingException e) {
                            throw new CustomException(ErrorCode.ENCODING_FAIL);
                        }
                    })
                    .toList();

            String message = restaurants.isEmpty()
                    ? "근처에 맛집이 없습니다."
                    : String.join(" / ", restaurants);
            restaurantMessages.put(type, message);
        }

        // 모든 카테고리별 기본 메시지 생성
        Map<String, String> defaultMessages = new HashMap<>();
        for (String category : categories) {
            defaultMessages.put(category, restaurantMessages.getOrDefault(category, "근처에 맛집이 없습니다."));
        }

        // 각 음식 카테고리별 메시지 내용 설정
        String koreanList = defaultMessages.get("한식");
        String japaneseList = defaultMessages.get("일식");
        String chineseList = defaultMessages.get("중국식");
        String chickenList = defaultMessages.get("호프/통닭");
        String etcList = defaultMessages.get("기타");

        // JSON 형식의 메시지 생성
        return String.format("""
                {
                    "username": "TastyTrack",
                    "avatar_url": "https://i.postimg.cc/t7000Q5P/test2.jpg",
                    "content": "오늘의 맛집을 추천해요😋",
                    "embeds": [
                        {
                            "author": {
                                "name": "All-Clear💎",
                                "icon_url": "https://i.postimg.cc/t7000Q5P/test2.jpg"
                            },
                            "title": "%s님께 보내는 맛집 Top5",
                            "description": "오늘의 점심 맛집을 추천해드립니다.😉",
                            "color": 3066993,
                            "fields": [
                                { 
                                    "name": "[참고📌]",
                                    "value": "- 폐업하거나, 잘못된 상호명이 있을 수 있습니다.\\n- 최신수정일자 + 평점순으로 추천합니다.",
                                    "inline": false
                                },
                                {
                                    "name": "[한식🍚]",
                                    "value": "%s",
                                    "inline": false
                                },
                                {
                                    "name": "[일식🍙]",
                                    "value": "%s",
                                    "inline": false
                                },
                                {
                                    "name": "[중국식🍜]",
                                    "value": "%s",
                                    "inline": false
                                },
                                {
                                    "name": "[호프/통닭🍻]",
                                    "value": "%s",
                                    "inline": false
                                },
                                {
                                    "name": "[기타☕]",
                                    "value": "%s",
                                    "inline": false
                                }
                            ]
                        }
                    ]
                }
                """, userInfo.getUsername(), koreanList, japaneseList, chineseList, chickenList, etcList);
    }

    /**
     * 디스코드 웹훅URL로 전송하는 메서드입니다.
     * 작성자: 배서진
     *
     * @param jsonBody
     */
    public void sendMessage(String jsonBody) {

        try {
            // RestTemplate 인스턴스 생성
            RestTemplate restTemplate = new RestTemplate();

            // URI 구성
            URI uri = UriComponentsBuilder.fromHttpUrl(webhookUrl)
                    .build()
                    .toUri();

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 요청 생성
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            log.info("디스코드 웹훅으로 메시지 전송 중...");


            // POST 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INVALID_DISCORD_MESSAGE);
        }

    }


}
