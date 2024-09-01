package com.allclear.tastytrack.domain.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 주기적으로 만료된 RefreshToken을 삭제합니다.
     * 매일 자정에 실행되도록 설정합니다.
     * 작성자 : 오예령
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void cleanUpExpiredTokens() {

        LocalDateTime now = LocalDateTime.now();
        // 만료된 토큰을 찾고 삭제합니다.
        refreshTokenRepository.deleteExpiredTokens(now);
    }

}
