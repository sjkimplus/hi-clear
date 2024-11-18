package com.play.hiclear.config;

import com.play.hiclear.common.batch.Tasklet.ReservationsTasklet;
import com.play.hiclear.common.batch.Tasklet.ScheduleTask;
import com.play.hiclear.domain.reservation.repository.ReservationRepository;
import com.play.hiclear.domain.schduleparticipant.repository.ScheduleParticipantRepository;
import com.play.hiclear.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleParticipantRepository scheduleParticipantRepository;
    private final PlatformTransactionManager transactionManager;

    // Tasklet 빈 등록
    @Bean
    public Tasklet deleteExpiredReservationsTasklet() {
        return new ReservationsTasklet(reservationRepository);  // 예약 만료 삭제 Tasklet
    }

    @Bean
    public Tasklet deleteExpiredSchedulesTasklet() {
        return new ScheduleTask(scheduleRepository, scheduleParticipantRepository);  // 모임 일정 만료 삭제 Tasklet
    }

    // Step 1: 예약 만료 처리
    @Bean
    public Step deleteExpiredReservationsStep() {
        log.info("Creating Step for Deleting Expired Reservations");

        return new StepBuilder("deleteExpiredReservationsStep", jobRepository)
                .tasklet(deleteExpiredReservationsTasklet(), transactionManager)
                .build();
    }

    // Step 2: 모임 일정 만료 처리
    @Bean
    public Step deleteExpiredSchedulesStep() {
        log.info("Creating Step for Deleting Expired Schedules");

        return new StepBuilder("deleteExpiredSchedulesStep", jobRepository)
                .tasklet(deleteExpiredSchedulesTasklet(), transactionManager)
                .build();
    }

    // Job 정의
    @Bean
    public Job deleteExpiredJob() {
        log.info("Creating Job to Delete Expired Reservations and Schedules");

        return new JobBuilder("deleteExpiredJob", jobRepository)
                .start(deleteExpiredReservationsStep())  // 첫 번째 Step: 예약 만료 처리
                .next(deleteExpiredSchedulesStep())     // 두 번째 Step: 모임 일정 만료 처리
                .preventRestart()  // 이 줄을 삭제하거나 수정하여 작업이 재시작 가능
                .build();
    }
}