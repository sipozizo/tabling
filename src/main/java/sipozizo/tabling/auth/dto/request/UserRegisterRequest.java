package sipozizo.tabling.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRegisterRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String address;
    @Email
    private String email;
    @NotBlank
    private String password;
}
