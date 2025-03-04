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

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservationServiceV2 {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final WaitingNumberServiceV2 waitingNumberService;
    private final StoreRepository storeRepository;

    @Transactional
    public Reservation createReservation(Long customerId, Long storeId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find customer with ID: " + customerId));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find store with ID: " + storeId));

        // Redis를 이용한 대기 번호 생성
        Integer newWaitingNumber = waitingNumberService.getNextWaitingNumber(storeId);

        Reservation newReservation = new Reservation(customer, ReservationStatus.WAITING, store);
        newReservation.setWaitingNumber(newWaitingNumber);
        reservationRepository.save(newReservation);

        // 수용량 확인 및 예약 상태 업데이트
        if (!isStoreAtCapacity(store)) {
            List<Reservation> waitingReservations = reservationRepository.findByStoreAndReservationStatusOrderByWaitingNumberAsc(
                    store, ReservationStatus.WAITING);
            if (waitingReservations.isEmpty() || waitingReservations.get(0).getId().equals(newReservation.getId())) {
                newReservation.updateReservationStatus(ReservationStatus.CALLED);
                reservationRepository.save(newReservation);
            }
        }

        return newReservation;
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByStoreAndStatus(Long storeId, ReservationStatus status) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find store with ID: " + storeId));
        return reservationRepository.findByStoreAndReservationStatus(store, status);
    }

    @Transactional
    public void seatReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("ID가 " + reservationId + "인 예약을 찾을 수 없습니다."));

        if (reservation.getReservationStatus() != ReservationStatus.CALLED) {
            throw new IllegalStateException("호출된 상태의 예약만 착석 처리할 수 있습니다.");
        }

        reservation.updateReservationStatus(ReservationStatus.SEATED);
        reservationRepository.save(reservation);

        handleAvailableCapacity(reservation.getStore());
    }

    @Transactional
    public void completeReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with ID: " + reservationId));

        if (reservation.getReservationStatus() != ReservationStatus.SEATED) {
            throw new IllegalStateException("Only seated reservations can be completed.");
        }

        reservation.updateReservationStatus(ReservationStatus.EMPTIED);
        reservationRepository.save(reservation);

        // 수용량 확인 및 대기 중인 예약 처리
        handleAvailableCapacity(reservation.getStore());
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with ID: " + reservationId));

        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED ||
                reservation.getReservationStatus() == ReservationStatus.EMPTIED) {
            throw new IllegalStateException("Reservation is already cancelled or emptied.");
        }

        reservation.updateReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // 수용량 확인 및 대기 중인 예약 처리
        handleAvailableCapacity(reservation.getStore());
    }

    // 수용량 확인 및 대기 중인 예약 호출
    private void handleAvailableCapacity(Store store) {
        if (!isStoreAtCapacity(store)) {
            // 대기 중인 예약 중 가장 오래된 예약을 CALLED로 변경
            Reservation nextReservation = reservationRepository.findFirstByStoreAndReservationStatusOrderByWaitingNumberAsc(
                    store, ReservationStatus.WAITING);
            if (nextReservation != null) {
                nextReservation.updateReservationStatus(ReservationStatus.CALLED);
                reservationRepository.save(nextReservation);
            }
        }
    }

    // 매장의 현재 수용 인원 확인 (SEATED + CALLED)
    private boolean isStoreAtCapacity(Store store) {
        int currentCount = reservationRepository.countByStoreAndReservationStatusIn(
                store, List.of(ReservationStatus.SEATED, ReservationStatus.CALLED));
        return currentCount >= store.getMaxSeatingCapacity();
    }
}