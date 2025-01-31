package sipozizo.tabling.entity;

// Store.java
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "Stores")
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user_id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "view")
    private int view;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    // Update methods

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public void updateView(int view) {
        this.view = view;
    }

    public void updateOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public void updateClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public void deleteStore() {
        this.isDeleted = true;
    }
}