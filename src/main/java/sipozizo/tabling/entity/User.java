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

    @NotBlank(message = "이름은 공백이어서 안 됩니다.")
    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @NotBlank(message = "이메일은 공백이어서는 안 됩니다.")
    @Email(message = "유효한 이메일 주소여야 합니다.")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "비밀번호는 공백이어서는 안됩니다.")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "유저 역할은 null일 수 없습니다.")
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