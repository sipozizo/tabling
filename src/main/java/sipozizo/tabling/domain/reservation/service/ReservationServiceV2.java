package sipozizo.tabling.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.common.entity.ReservationStatus;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.common.lock.service.LockService;
import sipozizo.tabling.domain.reservation.repository.ReservationRepository;
import sipozizo.tabling.domain.store.repository.StoreRepository;
import sipozizo.tabling.temp.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final LockService lockService;
    private final ReservationUpdateService reservationUpdateService;
    private final StoreRepository storeRepository;

    @Transactional
    public Reservation createReservation(Long reserverId, Long storeId) {
        User reserver = userRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reserver with ID: " + reserverId));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find store with ID: " + storeId));

        Reservation reservation = lockService.executeWithLock("store:" + storeId, () -> {
            Integer lastWaitingNumber = reservationRepository.findLastWaitingNumberByStore(store);
            Integer newWaitingNumber = (lastWaitingNumber == null) ? 1 : lastWaitingNumber + 1;

            Reservation newReservation = new Reservation(reserver, ReservationStatus.WAITING, store);
            newReservation.setWaitingNumber(newWaitingNumber);

            if (newWaitingNumber <= store.getMaxSeatingCapacity()) {
                newReservation.updateReservationStatus(ReservationStatus.CALLED);
            }

            return reservationRepository.save(newReservation);
        });

        return reservation;
    }

    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        String lockKey = "reservation:" + reservationId;
        lockService.executeWithLock(lockKey, () -> reservationUpdateService.updateStatus(reservationId, status));
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