package sipozizo.tabling.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.service.OwnerService;
import sipozizo.tabling.security.CustomUserDetails;

@RestController
@RequestMapping("/api/stores/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping
    public ResponseEntity<Void> createStore(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @Validated @RequestBody StoreRequest request) {
        ownerService.createStore(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
