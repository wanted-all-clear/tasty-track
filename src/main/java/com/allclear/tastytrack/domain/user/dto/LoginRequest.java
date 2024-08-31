package com.allclear.tastytrack.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {
    @NotNull(message = "사용자 이름은 필수 입력 값입니다.")
    private String username;
    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
}
