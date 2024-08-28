package com.allclear.tastytrack.domain.user.service;

import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;

public interface UserService {

    void signup(UserCreateRequest userCreateRequest);

}
