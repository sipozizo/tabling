package sipozizo.tabling.domain.store.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.common.exception.ErrorCode;
import sipozizo.tabling.common.exception.base.ConflictException;
import sipozizo.tabling.domain.store.model.request.StoreRequest;
import sipozizo.tabling.domain.store.repository.StoreRepository;

@Slf4j
@Service
@AllArgsConstructor
public class OwnerService {

    private final StoreRepository storeRepository;

    /**
     * 가게 생성 - V1, V2 형태 동일
     */
    @Transactional
    public void createStore(Long userId, StoreRequest request) {
        ;
        if (storeRepository.existsByStoreName(request.storeName())) {
            throw new ConflictException(ErrorCode.STORE_ALREADY_EXISTS);
        }

        if (storeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new ConflictException(ErrorCode.STORE_REGISTRATION_CONFLICT);
        }

        Store store = Store.builder()
                .storeName(request.storeName())
                .storeNumber(request.storeNumber())
                .storeAddress(request.storeAddress())
                .registrationNumber(request.registrationNumber())
                .openingTime(request.openingTime()) // NPE 방지
                .closingTime(request.closingTime())
                .category(request.category())
                .maxSeatingCapacity(request.maxSeatingCapacity())
                .build();

        storeRepository.save(store);
    }
}
