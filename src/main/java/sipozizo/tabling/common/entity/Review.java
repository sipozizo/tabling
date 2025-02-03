package sipozizo.tabling.common.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // reservation_id
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "star")
    private Star star;

    // Update methods

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateStar(Star star) {
        this.star = star;
    }
}