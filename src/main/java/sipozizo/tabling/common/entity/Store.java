package sipozizo.tabling.common.entity;

// Store.java
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Entity
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user_id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "address")
    private String storeAddress;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "view")
    private int view;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "category")
    private String category;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Builder
    public Store(String storeName, String storeNumber, String storeAddress, String registrationNumber, LocalTime openingTime, LocalTime closingTime, String category) {
        this.storeName = storeName;
        this.storeNumber = storeNumber;
        this.storeAddress = storeAddress;
        this.registrationNumber = registrationNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.category = category;
    }
}