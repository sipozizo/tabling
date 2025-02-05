package sipozizo.tabling.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.model.response.StoreResponse;
import sipozizo.tabling.domain.store.service.StoreService;
import sipozizo.tabling.security.CustomUserDetails;

import java.util.List;

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
        storeService.createStore(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * 캐싱 미적용 버전 가게 단건 조회 API (V1)
     */
    @GetMapping("/v1/{storeId}")
    public ResponseEntity<StoreResponse> getStoreV1(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable Long storeId) {
        StoreResponse response = storeService.getStoreByIdV1(storeId);
        return ResponseEntity.ok(response);
    }

    /**
     * 캐싱 미적용 버전 가게 전체 조회 API (V1)
     */
    @GetMapping("/v1")
    public ResponseEntity<Page<StoreResponse>> getAllStoresV1(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "35") int size) {
        Pageable pageable = PageRequest.of(page -1 ,size);

        Page<StoreResponse> response = storeService.getAllStoresV1(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 캐싱 적용 버전 가게 단건 조회 API (V2)
     */
    @GetMapping("/v2/{storeId}")
    public ResponseEntity<StoreResponse> getStoreV2(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable Long storeId) {
        StoreResponse response = storeService.getStoreByIdV2(storeId);
        return ResponseEntity.ok(response);
    }

    /**
     * 캐싱 적용 버전 가게 전체 조회 API (V2)
     */
    @GetMapping("/v2")
    public ResponseEntity<Page<StoreResponse>> getAllStoresV2(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "35") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<StoreResponse> response = storeService.getAllStoresV2(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 인기 검색어 조회 API
     */
    @GetMapping("/popular-keywords")
    public ResponseEntity<List<String>> getPopularKeywords() {
        List<String> keywords = storeService.getPopularKeywords();
        return ResponseEntity.ok(keywords);
    }
}
