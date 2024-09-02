package com.allclear.tastytrack.domain.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.allclear.tastytrack.domain.restaurant.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.allclear.tastytrack.domain.auth.UserDetailsImpl;
import com.allclear.tastytrack.domain.user.dto.UserInfo;
import com.allclear.tastytrack.domain.user.service.UserService;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RestaurantService restaurntService;
    @Mock
    private UserDetailsImpl mockUserDetails;

    private UserInfo mockUserInfo;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        mockUserInfo = UserInfo.builder()
                .username("testUser")
                .lon(127.0)
                .lat(37.5)
                .lunchRecommendYn(true)
                .build();

        // "testUser" 계정명을 가진 UserDetails Mock 객체 설정
        given(mockUserDetails.getUsername()).willReturn("testUser");

        // SecurityContext에 UserDetails Mock 객체 설정 => SecurityContext에 Authentication 객체를 설정하여, 이후 테스트에서 인증된 사용자의 정보를 사용할 수 있도록 함
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities()));

    }

    @Test
    @WithMockUser(username = "testUser")
    void getUserInfo() throws Exception {
        // given
        given(userService.getUserInfo(anyString())).willReturn(mockUserInfo);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.lon").value(127.0))
                .andExpect(jsonPath("$.lat").value(37.5))
                .andExpect(jsonPath("$.lunchRecommendYn").value(true));
    }

}
