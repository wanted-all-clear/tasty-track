package com.allclear.tastytrack.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {

        return new Info()
                .title("TastyTrack API Documentation")
                .description("원티드 프리온보딩 백엔드 인턴십 ALL_CLEAR 2주차 과제 API 문서")
                .version("1.0");
    }

}
