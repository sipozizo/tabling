package sipozizo.tabling.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // reserver_id
    @ManyToOne
    @JoinColumn(name = "reserver_id")
    private User reserver;

    @Column(name = "reservation_time")
    private LocalDateTime reservationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status")
    private ReservationStatus reservationStatus;

    // Update methods

    public void updateReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public void updateReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}