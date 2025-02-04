package sipozizo.tabling.domain.reservation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV1;
import sipozizo.tabling.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationControllerV1 {

    private final ReservationServiceV1 reservationServiceV1;

    // 예약 생성 API
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestParam Long storeId) {
        Reservation reservation = reservationServiceV1.createReservation(userDetails.getUserId(), storeId);
        return ResponseEntity.status(201).body(reservation);
    }

    // 예약 목록 조회 API
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Reservation>> getReservationsByStoreAndStatus(@PathVariable Long storeId,
                                                                             @RequestParam ReservationStatus status) {
        List<Reservation> reservations = reservationServiceV1.getReservationsByStoreAndStatus(storeId, status);
        return ResponseEntity.ok(reservations);
    }

    // 예약 완료 처리 API (식당 이용 완료)
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeReservation(@PathVariable Long id) {
        reservationServiceV1.completeReservation(id);
        return ResponseEntity.ok().build();
    }

    // 예약 취소 API
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationServiceV1.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
}