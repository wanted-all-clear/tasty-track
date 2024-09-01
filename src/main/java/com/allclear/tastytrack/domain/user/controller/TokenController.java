package com.allclear.tastytrack.domain.user.controller;

import com.allclear.tastytrack.domain.auth.token.RefreshTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenManager refreshTokenManager;

    @PostMapping("/api/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("RefreshToken") String refreshToken) {

        String newAccessToken = refreshTokenManager.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

}
