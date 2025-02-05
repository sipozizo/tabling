package sipozizo.tabling.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.domain.store.model.response.StoreWithViewCountResponseV1;
import sipozizo.tabling.domain.store.repository.StoreRepository;


@Service
@RequiredArgsConstructor
public class RedisStoreService {

    private final StoreRepository storeRepository;
    private final RedisTemplate<String, Integer> countRedisTemplate;
    private final RedisTemplate<String, Store> storeRedisTemplate;

    // 상점 조회(캐시 사용x)
    @Transactional
    public StoreWithViewCountResponseV1 getStoreV1(Long storeId) {
        Store store = storeRepository.findStoreById(storeId)
                .orElseThrow(() -> new IllegalStateException("찾는 상점이 없습니다."));
        store.incrementViewCount();
        return StoreWithViewCountResponseV1.of(store);
    }

}
