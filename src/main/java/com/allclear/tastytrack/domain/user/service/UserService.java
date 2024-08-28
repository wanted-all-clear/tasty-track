package com.allclear.tastytrack.domain.user.service;

import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import org.springframework.http.HttpHeaders;

public interface UserService {

    void signup(UserCreateRequest userCreateRequest);

    HttpHeaders signin(UserCreateRequest userCreateRequest);

}
