package sipozizo.tabling.domain.reservation.dto;

import lombok.Getter;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;

@Getter
public class ReservationResponse {
    private Long id;
    private Long reserverId;
    private Long storeId;
    private ReservationStatus reservationStatus;
    private Integer waitingNumber;
}