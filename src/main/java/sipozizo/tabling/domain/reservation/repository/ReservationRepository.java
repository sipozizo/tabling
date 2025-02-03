package sipozizo.tabling.domain.reservation.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sipozizo.tabling.common.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}