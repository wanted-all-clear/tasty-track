package com.allclear.tastytrack.domain.user.service;

import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import com.allclear.tastytrack.domain.user.dto.UserInfo;
import com.allclear.tastytrack.domain.user.dto.UserUpdateRequest;
import org.springframework.http.HttpHeaders;

public interface UserService {

    void signup(UserCreateRequest userCreateRequest);

    HttpHeaders signin(UserCreateRequest userCreateRequest);

    UserInfo updateUserInfo(String username, UserUpdateRequest userUpdateRequest);

    UserInfo getUserInfo(String username);

}
