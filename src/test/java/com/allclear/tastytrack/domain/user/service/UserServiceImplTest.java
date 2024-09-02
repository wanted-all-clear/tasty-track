package com.allclear.tastytrack.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.allclear.tastytrack.domain.user.dto.LoginRequest;
import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import com.allclear.tastytrack.domain.user.dto.UserInfo;
import com.allclear.tastytrack.domain.user.dto.UserUpdateRequest;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUP() {

        User user = User.builder()
                .id(1)
                .username("testUser")
                .password("testPassword")
                .lon(127.345)
                .lat(35.492)
                .lunchRecommendYn(false)
                .build();

        userRepository.save(user);
    }


    @Test
    @DisplayName("이미 가입된 계정명으로 회원가입을 합니다.")
    void signupDuplicateUsername() {

        //given
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .username("testUser")
                .password("password12")
                .lon(123.45)
                .lat(26.583)
                .build();

        // when & given
        CustomException exception = assertThrows(CustomException.class, () -> userService.signup(createRequest));
        assertEquals(ErrorCode.USERNAME_DUPLICATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("성공적으로 로그인합니다.")
    void signin() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .username("testUser")
                .password(passwordEncoder.encode("testPassword"))
                .build();
        // when
        HttpHeaders headers = userService.signin(loginRequest);

        // then
        assertNotNull(headers);
    }

    @Test
    @DisplayName("존재하지 않는 계정명으로 로그인합니다.")
    void signinWithNonExistUser() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .username("AnotherTestUser")
                .password(passwordEncoder.encode("testPassword"))
                .build();

        Exception exception = assertThrows(CustomException.class, () -> userService.signin(loginRequest));
        assertEquals(ErrorCode.USER_NOT_EXIST.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원정보를 수정합니다.")
    void updateUserInfo() {
        // given
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .lon(122.34)
                .lat(26.42)
                .lunchRecommendYn(true)
                .build();

        // when
        UserInfo user = userService.updateUserInfo("testUser", userUpdateRequest);

        User testUser = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_EXIST)
        );

        // then
        assertEquals(true, testUser.isLunchRecommendYn());
    }

}
