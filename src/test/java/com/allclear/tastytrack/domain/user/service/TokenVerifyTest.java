package com.allclear.tastytrack.domain.user.service;

import com.allclear.tastytrack.domain.auth.token.RefreshToken;
import com.allclear.tastytrack.domain.auth.token.RefreshTokenRepository;
import com.allclear.tastytrack.domain.user.dto.LoginRequest;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TokenVerifyTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("로그인 시 토큰을 발급합니다.")
    void generateAccessToken() {

        // given
        User user = User.builder()
                .username("testUser")
                .password("testPassword")
                .lon(123.45)
                .lat(26.583)
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .username("testUser")
                .password("testPassword")
                .build();

        // when
        HttpHeaders headers = userService.signin(loginRequest);

        // then
        String accessToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
        assertNotNull(accessToken);
        assertTrue(accessToken.startsWith("Bearer "));

        String refreshToken = headers.getFirst("RefreshToken");
        assertNotNull(refreshToken);
    }


    @Test
    @DisplayName("RefreshToken을 Redis에 저장합니다.")
    void saveRefreshTokenToRedis() {
        // given
        String key = "testKey";
        String username = "testUser";
        RefreshToken initRefreshToken = new RefreshToken(key, username);

        // when
        refreshTokenRepository.save(initRefreshToken);

        // then
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(key).orElseThrow(
                () -> new NullPointerException("해당 값이 존재하지 않습니다.")
        );
        assertThat(refreshToken.getUsername()).isEqualTo("testUser");
    }

}
