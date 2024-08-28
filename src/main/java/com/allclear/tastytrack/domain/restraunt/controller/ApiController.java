package com.allclear.tastytrack.domain.restraunt.controller;

import com.allclear.tastytrack.domain.restraunt.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class ApiController {

    private final ApiService apiService;

    // 맛집 데이터 수집
    // TODO: 1회 최대 요청 건수는 1000건입니다. 추후 예외 처리 필요합니다.
    @GetMapping("/{startIndex}/{endIndex}")
    public void getRawRestaurants(@PathVariable("startIndex") String startIndex,
                                    @PathVariable("endIndex") String endIndex) {

        apiService.getRawRestaurants(startIndex, endIndex);
    }
}
