package com.allclear.tastytrack.domain.auth.token;

import com.allclear.tastytrack.domain.auth.UserDetailsServiceImpl;
import com.allclear.tastytrack.global.exception.ErrorResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secretkey}")
    String JWT_SECRET;
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final UserDetailsServiceImpl userDetailsService;

    private static final String TOKEN_PREFIX = "Bearer ";

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        log.info("token : {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public Authentication getAuthentication(String token, HttpServletResponse response) throws IOException {

        UserDetails userDetails = userDetailsService.loadUserByUsername(decodeUsername(token, response));
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String decodeUsername(String token, HttpServletResponse response) throws IOException {

        DecodedJWT decodedJWT = isValidToken(token, response)
                .orElseThrow(() -> new IllegalArgumentException("유효한 토큰이 아닙니다."));

        return decodedJWT
                .getClaim(JwtTokenUtils.CLAIM_USERNAME)
                .asString();
    }

    // 토큰 유효성 검사
    public Optional<DecodedJWT> isValidToken(String token, HttpServletResponse response) throws IOException {

        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT
                    .require(generateAlgorithm(JWT_SECRET))
                    .build();
            jwt = verifier.verify(token);
        } catch (TokenExpiredException e) {
            logger.error(e.getMessage());
            tokenExpired(response);
        }
        return Optional.ofNullable(jwt);
    }

    // 만료된 토큰인 경우 error 처리
    public void tokenExpired(HttpServletResponse response) throws IOException {

        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다!");
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }

    private static Algorithm generateAlgorithm(String secretKey) {

        return Algorithm.HMAC256(secretKey.getBytes());
    }

}
