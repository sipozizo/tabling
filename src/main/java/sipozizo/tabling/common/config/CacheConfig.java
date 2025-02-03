package sipozizo.tabling.common.config;


/**
 * SpringBoot가 기본으로 사용하는 ConcurrentMapCacheManger가 만료 정책을 설정하는 것을 지원하지 않아
 * Caffeine 캐싱 라이브러리(CaffeineCacheManager)를 사용하도록 변경해주어야 함.
 * 이 클래스는 추후 만료 정책을 설정하기 위해 만들어둔 클래스
 */
public class CacheConfig {
}
