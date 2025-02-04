package sipozizo.tabling.domain.store.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.repository.StoreRepository;
import sipozizo.tabling.domain.user.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {

    private final CacheManager cacheManager;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStore(StoreRequest request) {

        if (storeRepository.existsByStoreName(request.storeName())) {
            throw new IllegalArgumentException("이미 존재하는 가게입니다.");
        }

        if (storeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new IllegalArgumentException("이미 존재하는 사업자등록번호입니다.");
        }

        Store store = Store.builder()
                .storeName(request.storeName())
                .storeNumber(request.storeNumber())
                .storeAddress(request.storeAddress())
                .registrationNumber(request.registrationNumber())
                .openingTime(request.openingTime().toLocalTime())
                .closingTime(request.closingTime().toLocalTime())
                .category(request.category())
                .build();

        storeRepository.save(store);
    }
}
