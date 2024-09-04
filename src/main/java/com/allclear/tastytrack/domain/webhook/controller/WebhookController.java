package com.allclear.tastytrack.domain.webhook.controller;

import com.allclear.tastytrack.domain.auth.UserDetailsImpl;
import com.allclear.tastytrack.domain.user.dto.UserInfo;
import com.allclear.tastytrack.domain.user.service.UserService;
import com.allclear.tastytrack.domain.webhook.service.DiscordWebhookService;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final UserService userService;
    private final DiscordWebhookService discordWebhookService;

    @PostMapping("/send")
    public String sendDiscordMessage(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserInfo userInfo = userService.getUserInfo(userDetails.getUsername());
        if (userInfo.isLunchRecommendYn()) {
            discordWebhookService.sendDailyMessage();
        }
        return "Discord로 점심 맛집 추천을 전송하였습니다.";
    }

}
