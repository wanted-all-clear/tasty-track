package com.allclear.tastytrack.domain.user.controller;

import com.allclear.tastytrack.domain.auth.UserDetailsImpl;
import com.allclear.tastytrack.domain.user.dto.LoginRequest;
import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import com.allclear.tastytrack.domain.user.dto.UserInfo;
import com.allclear.tastytrack.domain.user.dto.UserUpdateRequest;
import com.allclear.tastytrack.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> signup(@Validated @RequestBody UserCreateRequest userCreateRequest) {

        userService.signup(userCreateRequest);
        return ResponseEntity.status(201).body("회원가입이 완료되었습니다.");

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> signin(@Validated @RequestBody LoginRequest loginRequest) {

        HttpHeaders httpHeaders = userService.signin(loginRequest);

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(null);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<UserInfo> updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @Validated @RequestBody UserUpdateRequest userUpdateRequest) {

        return ResponseEntity.ok(userService.updateUserInfo(userDetails.getUsername(), userUpdateRequest));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<UserInfo> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(userService.getUserInfo(userDetails.getUsername()));
    }

}
