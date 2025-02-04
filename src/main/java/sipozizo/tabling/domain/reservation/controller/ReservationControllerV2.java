package sipozizo.tabling.domain.reservation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.common.entity.ReservationStatus;
import sipozizo.tabling.domain.reservation.service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성 API
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestParam Long reserverId,
                                                         @RequestParam Long storeId) {
        Reservation reservation = reservationService.createReservation(reserverId, storeId);
        return ResponseEntity.status(201).body(reservation);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Reservation>> getReservationsByStoreAndStatus(@PathVariable Long storeId,
                                                                             @RequestParam ReservationStatus status) {
        List<Reservation> reservations = reservationService.getReservationsByStoreAndStatus(storeId, status);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{id}/complete-customer")
    public ResponseEntity<Void> completePayment(@PathVariable Long id) {
        reservationService.completePayment(id);
        return ResponseEntity.ok().build();
    }
}