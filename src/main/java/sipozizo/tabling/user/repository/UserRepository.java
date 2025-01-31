package sipozizo.tabling.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sipozizo.tabling.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
