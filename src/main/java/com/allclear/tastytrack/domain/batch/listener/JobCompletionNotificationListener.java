package com.allclear.tastytrack.domain.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {

        jobStartTime = LocalDateTime.now();
        log.info("배치 작업 '{}' 시작 시간: {}", jobExecution.getJobInstance().getJobName(), jobStartTime);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        jobEndTime = LocalDateTime.now();
        log.info("배치 작업 '{}' 종료 시간: {}", jobExecution.getJobInstance().getJobName(), jobEndTime);
        log.info("배치 작업 '{}' 소요 시간: {} seconds", jobExecution.getJobInstance().getJobName(),
                java.time.Duration.between(jobStartTime, jobEndTime).getSeconds());
    }

}
