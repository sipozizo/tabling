package sipozizo.tabling.entity;


import jakarta.persistence.*;
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Users")
@Getter //추가
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 추가
    
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @NotBlank(message = "이름은 공백이어서 안 됩니다.") 검증 애노테이션은 DTO에서 적용
    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @NotBlank(message = "이메일은 공백이어서는 안 됩니다.")
    // @Email(message = "유효한 이메일 주소여야 합니다.") 검증 애노테이션은 DTO에서 적용
    @Column(name = "email", unique = true)
    private String email;

    // @NotBlank(message = "비밀번호는 공백이어서는 안됩니다.") 검증 애노테이션은 DTO에서 적용
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    // @NotNull(message = "유저 역할은 null일 수 없습니다.") 검증 애노테이션은 DTO에서 적용
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
------------------------------------------------------------------
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

------------------------------------------------------------------

    public void deleteUser() {
        this.isDeleted = true;
    }
}
