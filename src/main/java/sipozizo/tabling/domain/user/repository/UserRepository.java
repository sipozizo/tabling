package sipozizo.tabling.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sipozizo.tabling.common.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);
}
