package sipozizo.tabling.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.service.StoreService;
import sipozizo.tabling.security.CustomUserDetails;

@RestController
@RequestMapping("api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<Void> createStore(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @Validated @RequestBody StoreRequest request) {
        storeService.createStore(request);
        return ResponseEntity.ok().build();
    }
}
