package sipozizo.tabling.domain.reservation.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;
import sipozizo.tabling.common.entity.Store;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT MAX(r.waitingNumber) FROM Reservation r WHERE r.store = :store")
    Integer findLastWaitingNumberByStore(@Param("store") Store store);

    List<Reservation> findByStoreAndReservationStatus(Store store, ReservationStatus reservationStatus);

    int countByStoreAndReservationStatusIn(Store store, List<ReservationStatus> statuses);

    Reservation findFirstByStoreAndReservationStatusOrderByWaitingNumberAsc(Store store, ReservationStatus reservationStatus);

    @Query("SELECT MAX(r.waitingNumber) FROM Reservation r WHERE r.store = :store")
    Integer findMaxWaitingNumberByStore(@Param("store") Store store);

    List<Reservation> findByStoreAndReservationStatusOrderByWaitingNumberAsc(Store store, ReservationStatus status);
}