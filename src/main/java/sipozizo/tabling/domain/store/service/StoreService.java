package sipozizo.tabling.domain.store.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Cacheable(value = STORE_KEYWORD_CACHE, key = "#keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()") // 페이지는 계속 바뀌는데 키워드만으로 캐싱을 하기에는 무리가 있어서 에러가 발생했다..언더바로 더해서 페이지 넘버까지 같이 캐싱
    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStoresV2(String keyword, Pageable pageable) {
        log.info("검색 키워드 : {}", keyword);

        return findStoresByKeyword(keyword, pageable).map(StoreResponse::fromEntity);
    }

    /**
     * 캐시 삭제
     */
    @CacheEvict(value = STORE_CACHE, key = "#storeId") // String 값에서 storeId받도록 수정
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
}
