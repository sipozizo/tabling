package sipozizo.tabling.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sipozizo.tabling.common.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByStoreName(String storeName);

    boolean existsByRegistrationNumber(String registrationNumber);
}
