package com.allclear.tastytrack.domain.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ApiController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    // TODO: 테스트용으로 Controller API 호출중이며, 추후 스케줄링으로 전환할 예정입니다.
    @GetMapping("/first")
    public String firstApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("initRestaurantDataJob"), jobParameters);

        return "ok";
    }

}
