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

    public String generateJwtToken(String username) {
        String token;

        token = JWT.create()
                .withPayload(createClaims(username))
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                .sign(generateAlgorithm(JWT_SECRET));

        return token;
    }

    private Map<String, Object> createClaims(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USERNAME, username);

        return claims;
    }

    private static Algorithm generateAlgorithm(String secretKey) {
        return Algorithm.HMAC256(secretKey.getBytes());
    }
}
