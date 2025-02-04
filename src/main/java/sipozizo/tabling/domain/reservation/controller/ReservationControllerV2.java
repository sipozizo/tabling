package sipozizo.tabling.domain.reservation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.common.entity.ReservationStatus;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV2;

import java.util.List;

@RestController
@RequestMapping("/api/v2/reservations")
@RequiredArgsConstructor
public class ReservationControllerV2 {

    private final ReservationServiceV2 reservationServiceV2;

    // 예약 생성 API
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestParam Long reserverId,
                                                         @RequestParam Long storeId) {
        Reservation reservation = reservationServiceV2.createReservation(reserverId, storeId);
        return ResponseEntity.status(201).body(reservation);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Reservation>> getReservationsByStoreAndStatus(@PathVariable Long storeId,
                                                                             @RequestParam ReservationStatus status) {
        List<Reservation> reservations = reservationServiceV2.getReservationsByStoreAndStatus(storeId, status);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{id}/complete-customer")
    public ResponseEntity<Void> completePayment(@PathVariable Long id) {
        reservationServiceV2.completePayment(id);
        return ResponseEntity.ok().build();
    }
}