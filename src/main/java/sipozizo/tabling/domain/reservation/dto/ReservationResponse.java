package sipozizo.tabling.domain.reservation.dto;

import lombok.Getter;
import sipozizo.tabling.common.entity.ReservationStatus;

import java.time.LocalDateTime;

@Getter
public class ReservationResponse {
    private Long id;
    private Long reserverId;
    private Long storeId;
    private ReservationStatus reservationStatus;
    private Integer waitingNumber;
}