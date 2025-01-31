package sipozizo.tabling.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sipozizo.tabling.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);
}
