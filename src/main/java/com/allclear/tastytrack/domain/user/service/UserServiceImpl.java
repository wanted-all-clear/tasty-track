package com.allclear.tastytrack.domain.user.service;

import com.allclear.tastytrack.domain.auth.UserAuth;
import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
     * 계정명 중복체크
     * 작성자 : 오예령
     *
     * @param username 계정명
     */

    private void usernameDuplicateCheck(String username) {
        if (userRepository.findByUsername(username).isPresent())
            throw new CustomException(ErrorCode.USERNAME_DUPLICATION);
    }


}
