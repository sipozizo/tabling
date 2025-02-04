package sipozizo.tabling.common.lock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sipozizo.tabling.common.lock.repository.LockRedisRepository;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";

    public boolean tryLock(String key, long waitTime, long leaseTime) {
        String lockKey = LOCK_PREFIX + key;
        String uniqueId = UUID.randomUUID().toString();

        long endTime = System.currentTimeMillis() + waitTime;
        while (System.currentTimeMillis() < endTime) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, uniqueId, leaseTime, TimeUnit.MILLISECONDS);
            if (Boolean.TRUE.equals(success)) {
                // 락 획득 성공
                return true;
            }
            // 잠시 대기
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        // 락 획득 실패
        return false;
    }

    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end";
        String uniqueId = redisTemplate.opsForValue().get(lockKey);
        if (uniqueId != null) {
            redisTemplate.execute(
                    (connection, keys) -> connection.eval(
                            script.getBytes(),
                            ReturnType.INTEGER,
                            1,
                            lockKey.getBytes(),
                            uniqueId.getBytes()
                    ),
                    Collections.singletonList(lockKey),
                    Collections.singletonList(uniqueId)
            );
        }
    }
}