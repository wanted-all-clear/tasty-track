package com.allclear.tastytrack.domain.webhook.service;

import com.allclear.tastytrack.domain.region.entity.Region;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
public class DiscordWebhookService {

    // Discord 웹훅 URL
    @Value("${DISCORD_WEBHOOK_URL}")
    private String webhookUrl;

    private final RestaurantRepository restaurantRepository;
    private final RegionRepository regionRepository;

    /**
     * 스케쥴러를 평일 오전 11시 20분에 디스코드로 알림을 보냅니다.
     * 지역명은 랜덤으로 지정하여 발송하도록 합니다.
     * 작성자: 배서진
     */
    @Scheduled(cron = "0 20 11 * * MON-FRI")
    public void sendDailyMessage() {

        List<String> sggList = regionRepository.findAllSgg();
        if (sggList.isEmpty()) {
            throw new CustomException(ErrorCode.NO_REGION_DATA);
        }
        Random random = new Random();
        String randomSgg = sggList.get(random.nextInt(sggList.size()));

        sendMessage(randomSgg);
    }

    /**
     * 디스코드 웹훅으로 전송하는 메서드입니다.
     * 사용자 개개인이 아니라 디스코드 웹훅 URL로 전송하게 됩니다.
     * 작성자: 배서진
     *
     * @param content
     */
    public void sendMessage(String content) {


        Optional<Region> searchRegion = regionRepository.findByDosiOrSggContaining(content);
        Region region = searchRegion.orElseThrow(() -> new CustomException(ErrorCode.NO_REGION_DATA));

        double lat = region.getLat();
        double lon = region.getLon();
        double distance = 5.0; // 5km

        Map<String, String> restaurantMessages = new LinkedHashMap<>();


        for (String type : List.of("한식", "일식", "중국식", "호프/통닭", "기타")) {
            List<String> restaurants = restaurantRepository.findTop5ByTypeAndDistance(type, lon, lat, distance)
                    .stream()
                    .map(restaurant -> {
                        try {
                            String restaurantName = URLEncoder.encode(restaurant.getName(), "UTF-8");
                            if (restaurant.getName().contains(content)) {
                                // 가게명에 지역명이 포함되어 있는 경우, 지역명 없이 가게명에만 %20 적용
                                return "[" + restaurant.getName() + "](https://map.naver.com/p/search/" + restaurantName + ")";
                            } else {
                                // 가게명에 지역명이 포함되어 있지 않은 경우, content + 가게명
                                return "[" + restaurant.getName() + "](https://map.naver.com/p/search/" + URLEncoder.encode(content, "UTF-8") + "%20" + restaurantName + ")";
                            }
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

        String koreanList = restaurantMessages.getOrDefault("한식", "근처에 맛집이 없습니다.");
        String japaneseList = restaurantMessages.getOrDefault("일식", "근처에 맛집이 없습니다.");
        String chineseList = restaurantMessages.getOrDefault("중국식", "근처에 맛집이 없습니다.");
        String chickenList = restaurantMessages.getOrDefault("호프/통닭", "근처에 맛집이 없습니다.");
        String etcList = restaurantMessages.getOrDefault("기타", "근처에 맛집이 없습니다.");

        // JSON 형식의 임베드 메시지 생성
        String jsonBody = """
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
                            "title": "%s 맛집 Top5",
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
                """.formatted(content, koreanList, japaneseList, chineseList, chickenList, etcList);

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

        // POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        // 응답 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("메시지가 성공적으로 보내졌습니다.: " + response.getBody());
        } else {
            System.err.println("메시지 전송에 실패했습니다: : " + response.getBody());
        }
    }


}
