package sipozizo.tabling.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "Users")
@Getter //추가
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 추가
    
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @NotBlank(message = "이메일은 공백이어서는 안 됩니다.")
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    // 필요 생성자 추가
    public User(String name, String phoneNumber, String address, String email, String password, UserRole userRole) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    // 유저 회원가입(정적 팩토리 메서드)
    public static User registerUser(UserRegisterRequest request, String encodedPassword) {
        return new User(
                request.getName(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getEmail(),
                encodedPassword,
                UserRole.USER
            );
    }
    
    // 업데이트 수정본 (Patch)
    public void updateUser(UserUpdateRequest request) {
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getPhoneNumber() != null) {
            this.phoneNumber = request.getPhoneNumber();
        }
        if (request.getAddress() != null) {
            this.address = request.getAddress();
        }
        if (request.getEmail() != null) {
            this.email = request.getEmail();
        }
    }    

    // 비밀번호 업데이트
    public void updatePassword(String encodedPassword) {
        if (encodedPassword != null) {
            this.password = encodedPassword;
        }
    }

    // 권한 업데이트
    public void updateRole(UserRole userRole) {
        if (userRole != null {
            this.userRole = userRole;
        }
    }

    public void deleteUser() {
        this.isDeleted = true;
    }
}
