package sipozizo.tabling.auth.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sipozizo.tabling.auth.dto.request.UserLoginRequest;
import sipozizo.tabling.auth.dto.request.UserRegisterRequest;
import sipozizo.tabling.auth.dto.response.UserLoginResponse;
import sipozizo.tabling.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public void registerUser(@Valid @RequestBody UserRegisterRequest request) {
        authService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {

        UserLoginResponse loginResponse = authService.login(request);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
