package sipozizo.tabling.domain.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sipozizo.tabling.common.entity.Store;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByStoreName(String storeName);

    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<Store> findStoreById(Long id);

    @Query("SELECT s " +
            "FROM Store s " +
            "LEFT JOIN FETCH s.user u " +
            "WHERE s.category " +
            "LIKE %:keyword% ")
    Page<Store> findStoreByStoreCategory(@Param("keyword") String keyword, Pageable page);
}
