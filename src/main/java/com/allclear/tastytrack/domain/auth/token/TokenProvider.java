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
    private String JWT_SECRET;

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final UserDetailsServiceImpl userDetailsService;

    private static final String TOKEN_PREFIX = "Bearer "; // JWT 토큰의 접두사

    /**
     * HTTP 요청에서 JWT 토큰을 추출합니다.
     * 작성자 : 오예령
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 반환
     */
    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        log.info("token : {}", bearerToken);

        // Authorization 헤더가 존재하고, "Bearer "로 시작하는 경우 토큰을 추출
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * JWT 토큰을 기반으로 인증 정보를 생성합니다.
     * 작성자 : 오예령
     *
     * @param token JWT 토큰
     * @param response HTTP 응답 객체
     * @return Authentication 객체 반환
     * @throws IOException 예외 발생 시
     */
    public Authentication getAuthentication(String token, HttpServletResponse response) throws IOException {

        // 토큰에서 사용자 정보를 추출하여 UserDetails를 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(decodeUsername(token, response));
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

    /**
     * JWT 토큰에서 사용자 계정명을 추출합니다.
     * 작성자 : 오예령
     *
     * @param token JWT 토큰
     * @param response HTTP 응답 객체
     * @return 사용자 계정명 반환
     * @throws IOException 예외 발생 시
     */
    public String decodeUsername(String token, HttpServletResponse response) throws IOException {

        // 토큰의 유효성을 검사하고, 유효한 경우 사용자 계정명을 추출
        DecodedJWT decodedJWT = isValidToken(token, response)
                .orElseThrow(() -> new IllegalArgumentException("유효한 토큰이 아닙니다."));

        return decodedJWT
                .getClaim(JwtTokenUtils.CLAIM_USERNAME)
                .asString();
    }

    /**
     * JWT 토큰의 유효성을 검사합니다.
     * 작성자 : 오예령
     *
     * @param token JWT 토큰
     * @param response HTTP 응답 객체
     * @return 유효한 토큰을 DecodedJWT로 반환, 유효하지 않으면 Optional.empty()
     * @throws IOException 예외 발생 시
     */
    public Optional<DecodedJWT> isValidToken(String token, HttpServletResponse response) throws IOException {

        DecodedJWT jwt = null;
        try {
            // JWT 토큰 검증을 위한 JWTVerifier 생성
            JWTVerifier verifier = JWT
                    .require(generateAlgorithm(JWT_SECRET))
                    .build();
            jwt = verifier.verify(token);
        } catch (TokenExpiredException e) {
            // 토큰이 만료된 경우 에러 처리
            logger.error(e.getMessage());
            tokenExpired(response);
        }
        return Optional.ofNullable(jwt);
    }

    /**
     * 만료된 토큰에 대해 에러 응답을 생성합니다.
     * 작성자 : 오예령
     *
     * @param response HTTP 응답 객체
     * @throws IOException 예외 발생 시
     */
    public void tokenExpired(HttpServletResponse response) throws IOException {

        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }

    /**
     * HMAC256 알고리즘을 생성합니다.
     * 작성자 : 오예령
     *
     * @param secretKey JWT 비밀 키
     * @return HMAC256 알고리즘
     */
    private static Algorithm generateAlgorithm(String secretKey) {

        return Algorithm.HMAC256(secretKey.getBytes());
    }

}
