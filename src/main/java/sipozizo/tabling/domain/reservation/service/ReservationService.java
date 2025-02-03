package sipozizo.tabling.domain.reservation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.common.entity.ReservationStatus;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.domain.reservation.repository.ReservationRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reservation createReservation(Long reserverId, LocalDateTime reservationTime) {
        User reserver = userRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약자(ID: " + reserverId + ")를 찾을 수 없습니다."));

//       todo 추후 초기화 방법 정의 이후 변경하기
        Reservation reservation = new Reservation();
        reservation.setReserver(reserver);
        reservation.setReservationTime(reservationTime);
        reservation.setReservationStatus(ReservationStatus.WAITING);

        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약(ID: " + id + ")을 찾을 수 없습니다."));
    }
}