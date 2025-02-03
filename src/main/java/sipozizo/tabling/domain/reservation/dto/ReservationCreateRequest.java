package sipozizo.tabling.domain.reservation.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationCreateRequest {
    private Long reserverId;
    private LocalDateTime reservationTime;
}