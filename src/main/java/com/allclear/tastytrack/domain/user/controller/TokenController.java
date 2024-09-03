package com.allclear.tastytrack.domain.user.controller;

import com.allclear.tastytrack.domain.auth.token.RefreshTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenManager refreshTokenManager;

    @GetMapping("/api/refresh")
    public ResponseEntity<HttpHeaders> refreshAccessToken(@RequestHeader("RefreshToken") String refreshToken) {

        HttpHeaders headers = refreshTokenManager.refreshAccessToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(null);
    }

}
