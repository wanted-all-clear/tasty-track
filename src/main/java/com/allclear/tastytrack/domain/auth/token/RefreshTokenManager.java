package com.allclear.tastytrack.domain.auth.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenManager {
    
    private final static int TOKEN_LENGTH = 32; // Refresh Token의 길이를 설정
    private final SecureRandom secureRandom; // 난수 생성을 위한 SecureRandom 인스턴스
    private final RefreshTokenRepository refreshTokenRepository; // RefreshToken을 저장할 Repository


    /**
     * RefreshToken을 생성합니다.
     * TOKEN_LENGTH 길이의 난수를 생성하고, 이를 Base64 URL 형식으로 인코딩하여 반환합니다.
     * 작성자 : 오예령
     *
     * @return 생성된 RefreshToken 반환합니다.
     */
    public String refreshTokenGenerator() {

        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        // 생성된 난수를 Base64로 인코딩하여 Refresh Token 생성
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * 유저의 계정명으로 생성된 RefreshToken을 저장합니다.
     * 작성자 : 오예령
     *
     * @param username 유저 계정명
     * @return 저장된 RefreshToken을 반환합니다.
     */
    public String saveRefreshToken(final String username) {

        // 새로운 Refresh Token을 생성
        String refreshToken = refreshTokenGenerator();
        log.info("generated RefreshToken = {} ", refreshToken);

        // 생성된 토큰이 이미 존재하는지 확인
        if (refreshTokenRepository.findByUsername(refreshToken).isPresent()) {
            // 토큰이 이미 존재하면, 새로운 토큰을 재귀적으로 생성
            return saveRefreshToken(username);
        } else {
            // 토큰이 존재하지 않는 경우 해당 유저의 이름으로 새 토큰을 발급해 저장
            refreshTokenRepository.save(new RefreshToken(refreshToken, username));
        }

        // 최종적으로 저장된 RefreshToken을 반환
        return refreshToken;
    }


}
