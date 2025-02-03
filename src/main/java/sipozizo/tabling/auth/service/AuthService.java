package sipozizo.tabling.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.auth.dto.request.UserLoginRequest;
import sipozizo.tabling.auth.dto.request.UserRegisterRequest;
import sipozizo.tabling.auth.dto.response.UserLoginResponse;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.common.jwt.JwtUtil;
import sipozizo.tabling.domain.user.repository.UserRepository;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void registerUser(UserRegisterRequest request) {
        Boolean isExist = userRepository.existsByEmail(request.getEmail());

        if (isExist) {
            throw new IllegalStateException("이미 가입된 유저입니다.");
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());
        User user = User.registerUser(request, encodePassword);

        userRepository.save(user);
    }

    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getId(),user.getUserRole());

        return UserLoginResponse.from(user, token);
    }
}
