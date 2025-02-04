package sipozizo.tabling.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sipozizo.tabling.domain.store.model.response.StoreWithViewCountResponseV1;
import sipozizo.tabling.domain.store.service.RedisStoreService;

@RestController
@RequestMapping("/api/redis-stores")
@RequiredArgsConstructor
public class RedisStoreController {

    private final RedisStoreService redisStoreService;

    @GetMapping("/v1/{storeId}")
    public ResponseEntity<StoreWithViewCountResponseV1> getStoreV1(@PathVariable Long storeId) {
        return ResponseEntity.ok(redisStoreService.getStoreV1(storeId));
    }

}
