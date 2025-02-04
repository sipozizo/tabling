package sipozizo.tabling.domain.reservation.dto;

import lombok.Getter;

@Getter
public class ReservationCreateRequest {
    private Long reserverId;
    private Long storeId;
}