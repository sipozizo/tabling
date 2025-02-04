package sipozizo.tabling.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.model.response.StoreResponse;
import sipozizo.tabling.domain.store.service.StoreService;
import sipozizo.tabling.security.CustomUserDetails;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * 가게 생성 API - V1, V2 형태 동일
     */
    @PostMapping
    public ResponseEntity<Void> createStore(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Validated @RequestBody StoreRequest request) {
        storeService.createStore(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 캐싱 미적용 버전 (V1)
     */
    @GetMapping("/v1/{storeId}")
    public ResponseEntity<StoreResponse> getStoreV1(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable Long storeId) {
        StoreResponse response = storeService.getStoreByIdV1(storeId);
        return ResponseEntity.ok(response);
    }

    /**
     * 캐싱 적용 버전 (V2)
     */
    @GetMapping("/v2/{storeId}")
    public ResponseEntity<StoreResponse> getStoreV2(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable Long storeId) {
        StoreResponse response = storeService.getStoreByIdV2(storeId);
        return ResponseEntity.ok(response);
    }
}
