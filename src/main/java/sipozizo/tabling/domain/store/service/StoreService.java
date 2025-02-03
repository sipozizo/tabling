package sipozizo.tabling.domain.store.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.model.response.StoreResponse;
import sipozizo.tabling.domain.store.repository.StoreRepository;
import sipozizo.tabling.security.CustomUserDetails;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {
    private final CacheManager cacheManager;
    private final StoreRepository storeRepository;
    // private final UserRepository userRepository;

    public StoreResponse createStore(@AuthenticationPrincipal CustomUserDetails user, StoreRequest request) {
    }
}
