package com.allclear.tastytrack.domain.webhook.controller;

import com.allclear.tastytrack.domain.webhook.service.DiscordWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final DiscordWebhookService discordWebhookService;

    @PostMapping("/send")
    public String sendDiscordMessage(@RequestBody String message) {

        discordWebhookService.sendMessage(message);
        return "Discord로 점심 맛집 추천을 전송하였습니다.";
    }

}
