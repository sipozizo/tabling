package sipozizo.tabling.domain.reservation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV1;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationControllerV1 {

    private final ReservationServiceV1 reservationServiceV1;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestParam Long reserverId,
                                                         @RequestParam Long storeId) {
        Reservation reservation = reservationServiceV1.createReservation(reserverId, storeId);
        return ResponseEntity.status(201).body(reservation);
    }

}