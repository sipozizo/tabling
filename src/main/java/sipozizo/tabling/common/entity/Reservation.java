package sipozizo.tabling.common.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", length = 20)
    private ReservationStatus reservationStatus;

//    store 참조와 waiting_number 추가
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "waiting_number")
    private Integer waitingNumber;

//    TODO: 낙관적 락으로 테스트
    // @Version
    // private Long version;

    public Reservation(User customer,  ReservationStatus reservationStatus, Store store) {
        this.customer = customer;
        this.reservationStatus = reservationStatus;
        this.store = store;
    }

    // Update methods

    public void updateReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public void setWaitingNumber(Integer waitingNumber) {
        this.waitingNumber = waitingNumber;
    }
}
