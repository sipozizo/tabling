package sipozizo.tabling.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.domain.store.model.dto.StoreDto;
import sipozizo.tabling.domain.store.model.response.StoreWithViewCountResponseV1;
import sipozizo.tabling.domain.store.repository.StoreRepository;


import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisStoreService {

    private final StoreRepository storeRepository;
    private final RedisTemplate<String, Long> countRedisTemplate;
    private final RedisTemplate<String, StoreDto> storeRedisTemplate;


    // 상점 조회(캐시 사용x)
    @Transactional
    public StoreWithViewCountResponseV1 getStoreV1(Long storeId) {
        Store store = storeRepository.findStoreById(storeId)
                .orElseThrow(() -> new IllegalStateException("찾는 상점이 없습니다."));
        store.incrementViewCount();
        return StoreWithViewCountResponseV1.of(store);
    }


    // 상점 조회(캐시 사용)
    public StoreWithViewCountResponseV2 getStoreV2(Long storeId) {
        String redisKey = "store:" + storeId;

        Long viewCount = incrementViewCount(storeId);

        // 유효시간 10분
        StoreDto cachedStoreDto = storeRedisTemplate.opsForValue().get(redisKey);
        if (cachedStoreDto != null) {
            return StoreWithViewCountResponseV2.of(cachedStoreDto, viewCount);
        }

        Store store = storeRepository.findStoreById(storeId)
                .orElseThrow(() -> new IllegalStateException("찾는 상점이 없습니다."));
        StoreDto storeDto = StoreDto.of(store);

        storeRedisTemplate.opsForValue().set(redisKey, storeDto, 10, TimeUnit.MINUTES);

        return StoreWithViewCountResponseV2.of(storeDto, viewCount);
    }

    //조회수 카운팅(캐시 사용)
    private Long incrementViewCount(Long storeId) {
        String redisKey = "store:" + storeId + ":viewCount";
        Long viewCount = countRedisTemplate.opsForValue().increment(redisKey);

        Long ttl = countRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        if (ttl < 0) {
            countRedisTemplate.expire(redisKey, timeLeftUntilMidnight(), TimeUnit.SECONDS);
        }
        return viewCount;
    }


    // 자정시간 조회수 리셋 로직
    private long timeLeftUntilMidnight() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime timeNow = ZonedDateTime.now(zoneId);
        ZonedDateTime nextMidnight = timeNow.plusDays(1).toLocalDate().atStartOfDay(zoneId);
        return Duration.between(timeNow, nextMidnight).getSeconds();
    }

    // 레디스 캐시에 저장된 조회수가 최화 되기 5분 전에 DB에 저장
    @Scheduled(cron = "0 55 23 * * *")
    public void syncViewCountToDB() {
        Set<String> keys = countRedisTemplate.keys("store:*:viewCount");

        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String storeId = key.split(":")[1];
            Long viewCount = countRedisTemplate.opsForValue().get(key);

            if (viewCount != null) {
                // DB에 조회수 저장
                Store store = storeRepository.findById(Long.valueOf(storeId))
                        .orElseThrow(() -> new IllegalStateException("조회수 업데이트할 상점이 없습니다."));
                store.updateViewCount(viewCount); // 조회수 업데이트 메서드
                storeRepository.save(store);
            }
        }
    }

}
