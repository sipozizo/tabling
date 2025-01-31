package sipozizo.tabling.auth.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sipozizo.tabling.auth.dto.request.UserRegisterRequest;
import sipozizo.tabling.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public void registerUser(@RequestBody UserRegisterRequest request) {
        authService.registerUser(request);
    }
}
