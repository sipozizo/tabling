package sipozizo.tabling.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.common.entity.ReservationStatus;
import sipozizo.tabling.domain.reservation.repository.ReservationRepository;


@Service
@RequiredArgsConstructor
public class ReservationUpdateServiceV2 {

    private final ReservationRepository reservationRepository;
    private final PlatformTransactionManager transactionManager; // 추가

    public void updateStatus(Long reservationId, ReservationStatus status) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);

        transactionTemplate.executeWithoutResult(newStatus -> {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 예약(ID: " + reservationId + ")을 찾을 수 없습니다."));
            reservation.updateReservationStatus(status);
            reservationRepository.save(reservation);
        });
    }
}