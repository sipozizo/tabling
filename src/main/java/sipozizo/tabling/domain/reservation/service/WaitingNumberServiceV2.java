package sipozizo.tabling.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingNumberServiceV2 {

    private final StringRedisTemplate redisTemplate;

    public Integer getNextWaitingNumber(Long storeId) {
        String key = "waitingNumber:store:" + storeId;
        Long waitingNumber = redisTemplate.opsForValue().increment(key);
        return waitingNumber.intValue();
    }

    // 필요에 따라 초기화 메서드 추가 (예: 하루마다 리셋)
    public void resetWaitingNumber(Long storeId) {
        String key = "waitingNumber:store:" + storeId;
        redisTemplate.delete(key);
    }
}