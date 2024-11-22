package com.play.hiclear.common.batch.Tasklet;

import com.play.hiclear.domain.schduleparticipant.repository.ScheduleParticipantRepository;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ScheduleTask implements Tasklet {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleParticipantRepository scheduleParticipantRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("Starting expired schedule deletion...");

        // 현재 시간보다 지난 스케줄 삭제
        LocalDateTime now = LocalDateTime.now();

        // 만료된 스케줄들을 찾아서, 해당 스케줄에 연관된 참가자들도 삭제
        List<Schedule> expiredSchedules = scheduleRepository.findByEndTimeBefore(now);

        for (Schedule schedule : expiredSchedules) {
            // 연관된 참가자들을 삭제
            scheduleParticipantRepository.deleteParticipantsBySchedule(schedule);
            log.info("Deleted participants for schedule ID: {}", schedule.getId());
        }

        // 만료된 스케줄 삭제
        scheduleRepository.deleteExpiredSchedules(now);

        log.info("Expired schedule deletion finished.");
        return RepeatStatus.FINISHED;  // 작업 완료
    }
}
