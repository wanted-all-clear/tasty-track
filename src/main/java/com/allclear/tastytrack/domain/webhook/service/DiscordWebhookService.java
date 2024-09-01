package com.allclear.tastytrack.domain.webhook.service;

import com.allclear.tastytrack.domain.region.entity.Region;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscordWebhookService {

    // Discord 웹훅 URL
    @Value("${DISCORD_WEBHOOK_URL}")
    private String webhookUrl;

    private final RestaurantRepository restaurantRepository;
    private final RegionRepository regionRepository;

    public void sendMessage(String content) {


        Optional<Region> searchRegion = regionRepository.findByDosiOrSggContaining(content);

        double lat = searchRegion.get().getLat();
        double lon = searchRegion.get().getLon();
        double distance = 3.0;

        Map<String, String> restaurantMessages = new LinkedHashMap<>();


        for (String type : List.of("한식", "일식", "중국식", "호프/통닭", "기타")) {
            List<String> restaurants = restaurantRepository.findTop5ByTypeAndDistance(type, lon, lat, distance)
                    .stream()
                    .map(restaurant -> {
                        String restaurantName = restaurant.getName().replace(" ", "%20");
                        if (restaurant.getName().contains(content)) {
                            // 가게명에 지역명이 포함되어 있는 경우, 지역명 없이 가게명에만 %20 적용
                            return "[" + restaurant.getName() + "](https://map.naver.com/p/search/" + restaurantName + ")";
                        } else {
                            // 가게명에 지역명이 포함되어 있지 않은 경우, content + 가게명
                            return "[" + restaurant.getName() + "](https://map.naver.com/p/search/" + content.replace(" ", "%20") + "%20" + restaurantName + ")";
                        }
                    })
                    .toList();

            String message = (restaurants == null || restaurants.isEmpty())
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
