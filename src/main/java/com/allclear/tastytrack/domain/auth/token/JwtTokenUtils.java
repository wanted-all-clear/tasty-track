package com.allclear.tastytrack.domain.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    private static final int DAY = 24 * 60 * 60;
    // JWT 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int JWT_TOKEN_VALID_MILLI_SEC = 3 * DAY * 1000;
    public static final String CLAIM_USERNAME = "USERNAME";

    @Value("${jwt.secretkey}")
    String JWT_SECRET;

    /**
     * JwtToken을 생성합니다.
     * 생성된 토큰에는 payload, 토큰 만료일, 그리고 signature가 포함됩니다.
     * 작성자: 오예령
     *
     * @param username 유저 계정명
     * @return 해당 유저의 accessToken을 반환합니다.
     */
    public String generateJwtToken(String username) {

        String token;

        token = JWT.create()
                .withPayload(createClaims(username))
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                .sign(generateAlgorithm(JWT_SECRET));

        return token;
    }

    /**
     * 유저 계정명을 포함하는 claims 맵을 생성합니다.
     * 작성자 : 오예령
     *
     * @param username 유저 계정명
     * @return 유저 계정명이 포함된 key-value 형식의 claims 맵을 반환
     */
    private Map<String, Object> createClaims(String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USERNAME, username);

        return claims;
    }

    /**
     * HMAC256 형식의 알고리즘을 생성합니다.
     * 생성된 알고리즘은 JWT의 서명(Signature) 생성에 사용됩니다.
     * 작성자: 오예령
     *
     * @param secretKey JWT 서명에 사용할 secret key
     * @return HMAC256 알고리즘을 반환합니다.
     */
    private static Algorithm generateAlgorithm(String secretKey) {

        return Algorithm.HMAC256(secretKey.getBytes());
    }

}
