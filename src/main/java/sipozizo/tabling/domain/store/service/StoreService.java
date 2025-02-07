package sipozizo.tabling.domain.store.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.common.exception.ErrorCode;
import sipozizo.tabling.common.exception.base.ConflictException;
import sipozizo.tabling.common.exception.base.NotFoundException;
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
            throw new ConflictException(ErrorCode.STORE_ALREADY_EXISTS);
        }

        if (storeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new ConflictException(ErrorCode.STORE_REGISTRATION_CONFLICT);
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
     * 캐시 미적용 버전 가게 단건 조회 (V1)
     */
    @Transactional(readOnly = true)
    public StoreResponse getStoreByIdV1(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
        return StoreResponse.fromEntity(store);
    }

    /**
     * 캐시 미적용 버전 가게 전체 조회 (V1)
     */
    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStoresV1(String keyword, Pageable pageable) {
        return findStoresByKeyword(keyword, pageable)
                .map(StoreResponse::fromEntity);
    }

    /**
     * 캐시 적용 버전 가게 단건 조회 (V2)
     */
    @Cacheable(value = STORE_CACHE, key = "#storeId")
    @Transactional(readOnly = true)
    public StoreResponse getStoreByIdV2(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
        return StoreResponse.fromEntity(store);
    }

    /**
     * 캐시 적용 버전 가게 전체 조회 (V2)
     * TODO PageDTO 로 감싸기
     */
    @Cacheable(value = STORE_KEYWORD_CACHE, key = "#keyword + '_' + #pageable.pageNumber") // 페이지는 계속 바뀌는데 키워드만으로 캐싱을 하기에는 무리가 있어서 에러가 발생했다..언더바로 더해서 페이지 넘버까지 같이 캐싱
    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStoresV2(String keyword, Pageable pageable) { // TODO 레디스로 바꿀 때 직렬화 문제 해결하기
        log.info("검색 키워드 : {}", keyword);

        return findStoresByKeyword(keyword, pageable).map(StoreResponse::fromEntity);
    }

    /**
     * 캐시 삭제
     */
    @CacheEvict(value = STORE_CACHE, key = "storeId")
    public void clearStoreCache(Long storeId) {
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

    /**
     * keyword 비었는지 확인하는 method
     */
    private boolean isKeywordEmpty(String keyword) {
        return keyword == null || keyword.isBlank();
    }

    /**
     * 중복 if문 메서드로 뽑기
     */
    private Page<Store> findStoresByKeyword(String keyword, Pageable pageable) {
        if (!isKeywordEmpty(keyword)) {
            popularKeywordMap.merge(keyword, 1, Integer::sum);
            log.info("인기 검색어 저장 - {} (현재 검색 횟수: {})", keyword, popularKeywordMap.get(keyword));
        }
        return isKeywordEmpty(keyword)
                ? storeRepository.findAll(pageable)
                : storeRepository.findStoreByStoreCategory(keyword, pageable);
    }

    /**
     * 캐시 매니저를 통해서 인메모리 캐시 저장소에 담겨있는 데이터를 가시화 하는 로직
     */
    public void printCacheContents(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            log.info("현재 '{}' 캐시 내용:", cacheName);

            // 캐시가 stores(단건 조회)인지 popularKeywords(검색 캐시)인지 확인 후 데이터 조회
            if (STORE_CACHE.equals(cacheName)) {
                for (long storeId = 1; storeId <= 500; storeId++) {
                    Cache.ValueWrapper valueWrapper = cache.get(storeId);
                    if (valueWrapper != null) {
                        log.info("  - Key: {}, Value: {}", storeId, valueWrapper.get());
                    }
                }
            } else if (STORE_KEYWORD_CACHE.equals(cacheName)) {
                String[] sampleKeywords = {"한식", "중식", "양식", "일식"};
                for (String keyword : sampleKeywords) {
                    for (int page = 0; page < 100; page++) {
                        String key = keyword + "_" + page;
                        Cache.ValueWrapper valueWrapper = cache.get(key);
                        if (valueWrapper != null) {
                            log.info("  - Key: {}, Value: {}", key, valueWrapper.get());
                        }
                    }
                }
            }
        } else {
            log.warn("'{}' 캐시가 존재하지 않습니다.", cacheName);
        }
    }
}
