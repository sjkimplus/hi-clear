package com.play.hiclear.domain.reservation.dto.response;

import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ReservationSearchDetailResponse {
    private Long id;
    private String email;
    private TimeSlotResponse timeSlot;
    private CourtResponse court;
    private String status;

    public static ReservationSearchDetailResponse from(Reservation reservation) {
        ReservationSearchDetailResponse response = new ReservationSearchDetailResponse();
        response.id = reservation.getId();
        response.email = reservation.getUser().getEmail();
        response.timeSlot = new TimeSlotResponse(reservation.getTimeSlot());
        response.court = new CourtResponse(reservation.getCourt());
        response.status = reservation.getStatus().name();
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

    @Getter
    @NoArgsConstructor
    public static class CourtResponse {
        private Long courtNum;
        private int price;

        public CourtResponse(Court court) {
            this.courtNum = court.getCourtNum();
            this.price = court.getPrice();
        }
    }
}