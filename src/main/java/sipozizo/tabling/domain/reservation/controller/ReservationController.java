package sipozizo.tabling.domain.reservation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.domain.reservation.service.ReservationService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성 API
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestParam Long reserverId,
                                                         @RequestParam LocalDateTime reservationTime) {
        Reservation reservation = reservationService.createReservation(reserverId, reservationTime);
        return ResponseEntity.status(201).body(reservation);
    }

    // 예약 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservation(id);
        return ResponseEntity.ok(reservation);
    }
}