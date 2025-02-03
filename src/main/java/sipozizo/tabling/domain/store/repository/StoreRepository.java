package sipozizo.tabling.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sipozizo.tabling.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
