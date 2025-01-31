package sipozizo.tabling.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Password should not be blank")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "User role should not be null")
    @Column(name = "user_role")
    private UserRole userRole;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    // Update methods

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void deleteUser() {
        this.isDeleted = true;
    }
}