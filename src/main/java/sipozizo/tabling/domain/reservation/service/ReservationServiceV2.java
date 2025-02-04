package sipozizo.tabling.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.common.lock.service.RedisLockServiceV3;
import sipozizo.tabling.domain.reservation.repository.ReservationRepository;
import sipozizo.tabling.domain.store.repository.StoreRepository;
import sipozizo.tabling.domain.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservationServiceV2 {

    private final RedisLockServiceV3 redisLockServiceV3;
    private final ReservationUpdateServiceV2 reservationUpdateServiceV2;

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final WaitingNumberServiceV2 waitingNumberService; // 추가
    private final StoreRepository storeRepository;

    @Transactional
    public Reservation createReservation(Long reserverId, Long storeId) {
        User reserver = userRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reserver with ID: " + reserverId));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find store with ID: " + storeId));

//        원자적 증가 연산('INCR' 명령) 사용
        Integer newWaitingNumber = waitingNumberService.getNextWaitingNumber(storeId);

        Reservation newReservation = new Reservation(reserver, ReservationStatus.WAITING, store);
        newReservation.setWaitingNumber(newWaitingNumber);

        if (newWaitingNumber <= store.getMaxSeatingCapacity()) {
            newReservation.updateReservationStatus(ReservationStatus.CALLED);
        }

        return reservationRepository.save(newReservation);
    }

    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        String lockKey = "reservation:" + reservationId;
//        TODO: 분산 락에 맞게 추후 수정
//        redisLockServiceV3.executeWithLock(lockKey, () -> reservationUpdateServiceV2.updateStatus(reservationId, status));
    }


    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByStoreAndStatus(Long storeId, ReservationStatus status) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find store with ID: " + storeId));
        return reservationRepository.findByStoreAndReservationStatus(store, status);
    }

    @Transactional
    public void completePayment(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with ID: " + reservationId));

        if (reservation.getReservationStatus() != ReservationStatus.SEATED) {
            throw new IllegalStateException("Only seated reservations can be completed.");
        }

        reservation.updateReservationStatus(ReservationStatus.EMPTIED);
        reservationRepository.save(reservation);

        callNextCustomer(reservation.getStore());
    }

    private void callNextCustomer(Store store) {
        Reservation nextReservation = reservationRepository.findFirstByStoreAndReservationStatusOrderByWaitingNumberAsc(
                store, ReservationStatus.WAITING);
        if (nextReservation != null) {
            nextReservation.updateReservationStatus(ReservationStatus.CALLED);
            reservationRepository.save(nextReservation);
        }
    }
}