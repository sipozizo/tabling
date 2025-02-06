package sipozizo.tabling.common.lock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sipozizo.tabling.common.entity.BaseEntity;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;

@Entity
@Table(name = "optimistic_reservations")
@Getter
@NoArgsConstructor
public class OptimisticReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약자
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", length = 20)
    private ReservationStatus reservationStatus;

    // 매장 참조 및 대기 번호
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "waiting_number")
    private Integer waitingNumber;

    // 낙관적 락 버전 필드 추가
    @Version
    private Long version;

    public OptimisticReservation(User customer, ReservationStatus reservationStatus, Store store) {
        this.customer = customer;
        this.reservationStatus = reservationStatus;
        this.store = store;
    }

    // 상태 업데이트 메소드
    public void updateReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public void setWaitingNumber(Integer waitingNumber) {
        this.waitingNumber = waitingNumber;
    }
}