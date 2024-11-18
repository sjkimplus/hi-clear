package com.play.hiclear.common.batch.Scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteExpiredJob;

    // 매일 자정에 배치 작업 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void executeBatchJob() {
        log.info("Executing batch job for deleting expired reservations and schedules...");

        try {
            // JobParameters에 현재 타임스탬프를 넣어 고유한 값을 부여
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(deleteExpiredJob, jobParameters);
            log.info("Batch job executed successfully.");
        } catch (Exception e) {
            log.error("Failed to execute batch job", e);
        }
    }
}