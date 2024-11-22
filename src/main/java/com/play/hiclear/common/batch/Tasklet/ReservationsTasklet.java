package com.play.hiclear.common.batch.Tasklet;

import com.play.hiclear.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
public class ReservationsTasklet implements Tasklet {

    private final ReservationRepository reservationRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("Starting expired reservation deletion...");

        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = now.toLocalDate();  // 현재 날짜
        LocalTime nowTime = now.toLocalTime();  // 현재 시간

        // 예약 만료 처리
        reservationRepository.deleteExpiredReservations(nowDate, nowTime);

        log.info("Expired reservation deletion finished.");
        return RepeatStatus.FINISHED;  // 작업 완료
    }
}