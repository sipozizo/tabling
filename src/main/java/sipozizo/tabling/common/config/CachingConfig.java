package sipozizo.tabling.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 캐시 사용을 위한 캐싱컨피그 클래스
 */

@EnableCaching
@Configuration
public class CachingConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(); // 추후 Cache V2로 바꿀 때 이 부분만 RedisCacheManager 로 변경해주기만 하면 된다.
    }
}
