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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {

    private static final String STORE_CACHE = "stores";
    private static final String STORE_KEYWORD_CACHE = "popularKeywords";
    private final ConcurrentHashMap<String, Integer> popularKeywordMap = new ConcurrentHashMap<>();

    private final CacheManager cacheManager;
    private final StoreRepository storeRepository;

    /**
     * 가게 생성 - V1, V2 형태 동일
     */
    @Transactional
    public void createStore(Long userId, StoreRequest request) {

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
                .build();

        storeRepository.save(store);
    }

    /**
     * 캐시 미적용 버전 가게 단건 조회 (V1)
     */
    @Transactional(readOnly = true)
    public StoreResponse getStoreByIdV1(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 맞는 가게가 존재하지 않습니다."));
        return StoreResponse.fromEntity(store);
    }

    /**
     * 캐시 미적용 버전 가게 전체 조회 (V1)
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
     * 캐시 적용 버전 가게 단건 조회 (V2)
     */
    @Cacheable(value = STORE_CACHE, key = "#storeId")
    @Transactional(readOnly = true)
    public StoreResponse getStoreByIdV2(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 맞는 가게가 존재하지 않습니다.")); //todo 예외 처리 변경 예정
        return StoreResponse.fromEntity(store);
    }

    /**
     * 캐시 적용 버전 가게 전체 조회 (V2)
     * TODO PageDTO 로 감싸기
     */
    @Cacheable(value = STORE_KEYWORD_CACHE, key = "#keyword + '_' + #pageable.pageNumber") // 페이지는 계속 바뀌는데 키워드만으로 캐싱을 하기에는 무리가 있어서 에러가 발생했다..언더바로 더해서 페이지 넘버까지 같이 캐싱
    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStoresV2(String keyword, Pageable pageable) { // TODO 레디스로 바꿀 때 직렬화 문제 해결하기

        if (keyword != null && !keyword.isBlank()) {
            savePopularKeyword(keyword);
        }

        Page<Store> stores;
        if (keyword == null || keyword.isBlank()) {
            stores = storeRepository.findAll(pageable);
        } else {
            stores = storeRepository.findStoreByStoreCategory(keyword, pageable);
        }

        return stores.map(StoreResponse::fromEntity);
    }

    /**
     * 인기 검색어 저장 (로컬 메모리)
     */
    private void savePopularKeyword(String keyword) {
        popularKeywordMap.merge(keyword, 1, Integer::sum);
    }

    /**
     * 인기 검색어 조회 (Top 5 반환)
     */
    public List<String> getPopularKeywords() {
        return popularKeywordMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // 검색 횟수 기준 내림차순 정렬
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
