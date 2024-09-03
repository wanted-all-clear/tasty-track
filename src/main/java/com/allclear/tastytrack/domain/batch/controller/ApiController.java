package com.allclear.tastytrack.domain.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fetch")
public class ApiController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    // TODO: 테스트용으로 Controller API 호출중이며, 추후 스케줄링으로 전환할 예정입니다.
    @GetMapping("/{value}")
    public ResponseEntity<String> fetchRestaurant(@PathVariable("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("fetchJob"), jobParameters);

        return ResponseEntity.ok("배치 작업이 완료되었습니다.");
    }

}
