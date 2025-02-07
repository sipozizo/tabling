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
public class ReservationServiceV3 {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;
    private final RedisLockServiceV3 lockService;
    private final ReservationUpdateServiceV2 reservationUpdateServiceV2;

//    @Transactional
    public Reservation createReservation(Long reserverId, Long storeId) {
        // 락 키 설정 (예: 매장별로 락)
        String lockKey = "store:" + storeId;
        boolean isLocked = lockService.tryLock(lockKey, 100000, 100000);
        if (!isLocked) {
            throw new RuntimeException("Failed to acquire lock for store: " + storeId);
        }
        try {
            // 예약자와 매장 정보 조회
            User reserver = userRepository.findById(reserverId)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find reserver with ID: " + reserverId));

            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find store with ID: " + storeId));

            // 현재 매장의 최대 대기 번호 조회
            Integer maxWaitingNumber = reservationRepository.findMaxWaitingNumberByStore(store);

            // 새로운 대기 번호 설정
            Integer newWaitingNumber = (maxWaitingNumber == null) ? 1 : maxWaitingNumber + 1;

            Reservation newReservation = new Reservation(reserver, ReservationStatus.WAITING, store);
            newReservation.setWaitingNumber(newWaitingNumber);

            if (newWaitingNumber <= store.getMaxSeatingCapacity()) {
                newReservation.updateReservationStatus(ReservationStatus.CALLED);
            }

            return reservationRepository.save(newReservation);
        } finally {
            // 락 해제
            lockService.unlock(lockKey);
        }
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