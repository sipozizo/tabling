package sipozizo.tabling.common.lock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisLockServiceV3 {

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
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);

            // 스크립트 실행
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), uniqueId);

            // 필요한 경우 결과 확인
            if (result != null && result > 0) {
                // 락 해제 성공
            } else {
                // 락 해제 실패 또는 이미 해제됨
            }
        }
    }
}