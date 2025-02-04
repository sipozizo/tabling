package sipozizo.tabling.domain.reservation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.domain.reservation.repository.ReservationRepository;
import sipozizo.tabling.domain.store.repository.StoreRepository;
import sipozizo.tabling.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReservationServiceV1 {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Reservation createReservation(Long reserverId, Long storeId) {
        User reserver = userRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reserver with ID: " + reserverId));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find store with ID: " + storeId));

        // 대기 번호 생성 및 예약 상태 설정
        // 현재 해당 매장의 최대 대기 번호 조회
        Integer lastWaitingNumber = reservationRepository.findLastWaitingNumberByStore(store);
        Integer newWaitingNumber = (lastWaitingNumber == null) ? 1 : lastWaitingNumber + 1;

        Reservation newReservation = new Reservation(reserver, ReservationStatus.WAITING, store);
        newReservation.setWaitingNumber(newWaitingNumber);

        // 대기 번호가 최대 착석 인원 이내인 경우, 상태를 CALLED로 변경
        if (newWaitingNumber <= store.getMaxSeatingCapacity()) {
            newReservation.updateReservationStatus(ReservationStatus.CALLED);
        }

        // 동시성 제어 없이 바로 저장
        return reservationRepository.save(newReservation);
    }

    @Transactional
    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with ID: " + reservationId));
        reservation.updateReservationStatus(status);
        reservationRepository.save(reservation);
    }

    // 기타 필요한 메서드들...
}