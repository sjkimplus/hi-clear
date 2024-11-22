package com.play.hiclear.domain.reservation.dto.response;

import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ReservationSearchResponse {
    private Long id;
    private String email;
    private TimeSlotResponse timeSlot;
    private String status;
    private LocalDate date;

    public static ReservationSearchResponse from(Reservation reservation) {
        ReservationSearchResponse response = new ReservationSearchResponse();
        response.id = reservation.getId();
        response.email = reservation.getUser().getEmail();
        response.timeSlot = new TimeSlotResponse(reservation.getTimeSlot());
        response.status = reservation.getStatus().name();
        response.date = reservation.getDate();
        return response;
    }

    @Getter
    @NoArgsConstructor
    public static class TimeSlotResponse {
        private LocalTime startTime;
        private LocalTime  endTime;

        public TimeSlotResponse(TimeSlot timeSlot) {
            this.startTime = timeSlot.getStartTime();
            this.endTime = timeSlot.getEndTime();
        }
    }
}
