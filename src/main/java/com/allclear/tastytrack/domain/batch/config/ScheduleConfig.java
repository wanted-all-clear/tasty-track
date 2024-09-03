package com.allclear.tastytrack.domain.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ScheduleConfig {

    // TODO: 배치 API 호출 방식을 추후 스케줄링 방식으로 전환할 예정입니다.
//    private final JobLauncher jobLauncher;
//    private final JobRegistry jobRegistry;
//
//    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 한국 시간 기준으로 매일 자정에 실행
//    public void runFirstJob() throws Exception {
//
//        log.info("first schedule start");
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//        String date = dateFormat.format(new Date());
//
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("date", date)
//                .toJobParameters();
//
//        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);
//    }

}
