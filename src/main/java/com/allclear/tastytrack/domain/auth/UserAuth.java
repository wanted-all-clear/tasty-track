package com.allclear.tastytrack.domain.auth;

import com.allclear.tastytrack.domain.user.entity.User;
import org.springframework.http.HttpHeaders;

public interface UserAuth {

    String getToken(User user);
    String saveRefreshTokenToRedis(User user);
    HttpHeaders generateHeaderTokens(User user);

}
