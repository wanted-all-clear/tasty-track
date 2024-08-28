package com.allclear.tastytrack.domain.user.service;

import com.allclear.tastytrack.domain.auth.UserAuth;
import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import com.allclear.tastytrack.domain.user.dto.UserInfo;
import com.allclear.tastytrack.domain.user.dto.UserUpdateRequest;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuth userAuth;

    /**
     * 회원가입
     * 작성자 : 오예령
     *
     * @param userCreateRequest 계정명, 비밀번호, 위도, 경도를 등록
     */
    @Override
    @Transactional
    public void signup(UserCreateRequest userCreateRequest) {
        usernameDuplicateCheck(userCreateRequest.getUsername());
        User user = User.builder()
                .username(userCreateRequest.getUsername())
                .password(passwordEncoder.encode(userCreateRequest.getPassword()))
                .lon(userCreateRequest.getLon())
                .lat(userCreateRequest.getLat())
                .build();
        userRepository.save(user);
    }

    /**
     * 로그인
     * 작성자 : 오예령
     *
     * @param userCreateRequest 계정명, 비밀번호
     * @return 헤더에 토큰을 담아 반환
     */
    @Override
    @Transactional
    public HttpHeaders signin(UserCreateRequest userCreateRequest) {

        User user = userCheck(userCreateRequest.getUsername());
        return userAuth.generateHeaderTokens(user);
    }

    /**
     * 회원정보 수정
     * 작성자 : 오예령
     *
     * @param username          계정명
     * @param userUpdateRequest 위도, 경도
     * @return 수정된 회원정보 반환
     */
    @Override
    @Transactional
    public UserInfo updateUserInfo(String username, UserUpdateRequest userUpdateRequest) {

        User user = userCheck(username);
        User initUser = userUpdateRequest.toUpdateEntity(user);
        user.update(initUser);
        return new UserInfo(user);
    }

    /**
     * 계정명 중복체크
     * 작성자 : 오예령
     *
     * @param username 계정명
     */

    private void usernameDuplicateCheck(String username) {
        if (userRepository.findByUsername(username).isPresent())
            throw new CustomException(ErrorCode.USERNAME_DUPLICATION);
    }

    /**
     * 회원 검증
     * 작성자 : 오예령
     *
     * @param username 계정명
     * @return 회원 객체 반환
     */
    private User userCheck(String username) {

        return userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_EXIST)
        );
    }

}
