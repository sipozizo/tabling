package sipozizo.tabling.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.auth.dto.request.UserRegisterRequest;
import sipozizo.tabling.user.entity.User;
import sipozizo.tabling.user.repository.UserRepository;

@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserRegisterRequest request) {
        Boolean isExist = userRepository.findByEmail(request.getEmail());

        if (isExist) {
            throw new IllegalStateException("이미 가입된 유저입니다.");
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());
        User user = User.registerUser(request, encodePassword);

        userRepository.save(user);
    }
}
