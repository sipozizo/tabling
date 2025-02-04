package sipozizo.tabling.domain.store.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.model.response.StoreResponse;
import sipozizo.tabling.domain.store.repository.StoreRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {

    private static final String STORE_CACHE = "stores";
    private final CacheManager cacheManager;
    private final StoreRepository storeRepository;

    /**
     * 가게 생성 - V1, V2 형태 동일
     */
    @Transactional
    public void createStore(StoreRequest request) {

        if (storeRepository.existsByStoreName(request.storeName())) {
            throw new IllegalArgumentException("이미 존재하는 가게입니다."); //todo 예외처리 변경 예정
        }

        if (storeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new IllegalArgumentException("이미 존재하는 사업자등록번호입니다."); // todo 예외처리 변경 예정
        }

        Store store = Store.builder()
                .storeName(request.storeName())
                .storeNumber(request.storeNumber())
                .storeAddress(request.storeAddress())
                .registrationNumber(request.registrationNumber())
                .openingTime(request.openingTime()) // NPE 방지
                .closingTime(request.closingTime())
                .category(request.category())
                .maxSeatingCapacity(request.maxSeatingCapacity())
                .build();

        storeRepository.save(store);
    }

    /**
     * 캐시 미적용 버전 (V1)
     */
    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStoresV1(String keyword, Pageable pageable) {
        Page<Store> stores;

        if (keyword == null || keyword.isEmpty()) {
            stores = storeRepository.findAll(pageable);
        } else {
            stores = storeRepository.findStoreByStoreCategory(keyword, pageable);
        }

        return stores.map(store -> new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreNumber(),
                store.getStoreAddress(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getCategory()
        ));
    }

    /**
     * 가게 단건 조회 (V1)
     */
    @Transactional(readOnly = true)
    public StoreResponse getStoreByIdV1(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 맞는 가게가 존재하지 않습니다."));
        return StoreResponse.fromEntity(store);
    }

    /**
     * 캐시 적용 버전 (V2)
     */
    public void getAllStoresV2() {
    }

    /**
     * 가게 단건 조회 (V2)
     */
    @Cacheable(value = STORE_CACHE, key = "#storeId")
    @Transactional(readOnly = true)
    public StoreResponse getStoreByIdV2(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 맞는 가게가 존재하지 않습니다.")); //todo 예외 처리 변경 예정
        return StoreResponse.fromEntity(store);
    }
}
