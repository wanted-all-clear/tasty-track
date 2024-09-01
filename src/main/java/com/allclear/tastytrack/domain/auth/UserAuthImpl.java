package com.allclear.tastytrack.domain.auth;

import com.allclear.tastytrack.domain.auth.token.JwtTokenUtils;
import com.allclear.tastytrack.domain.auth.token.RefreshTokenManager;
import com.allclear.tastytrack.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthImpl implements UserAuth {
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenManager refreshTokenManager;

    /**
     * JwtToken 생성합니다.
     * 작성자 : 오예령
     *
     * @param user 유저 객체
     * @return 해당 유저의 accessToken 반환합니다.
     */
    @Override
    public String getToken(User user) {
        return jwtTokenUtils.generateJwtToken(user.getUsername());
    }

    /**
     * RefreshToken을 생성하고 Redis에 저장합니다.
     * 작성자: 오예령
     *
     * @param user 유저 객체
     * @return 해당 유저의 refreshToken을 반환합니다.
     */
    @Override
    public String saveRefreshTokenToRedis(User user){
        return refreshTokenManager.saveRefreshToken(user.getUsername());
    }

    /**
     * 유저의 헤더에 accessToken과 refreshToken을 담아 반환합니다.
     * 작성자: 오예령
     *
     * @param user 유저 객체
     * @return 토큰이 담긴 헤더 반환합니다.
     */
    @Override
    public HttpHeaders generateHeaderTokens(User user) {
        HttpHeaders headers = new HttpHeaders();  // 여기서 HttpHeaders 객체를 생성
        String accessToken = getToken(user);
        String refreshToken = saveRefreshTokenToRedis(user);
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + accessToken);
        headers.set("RefreshToken", refreshToken);
        return headers;
    }


}
