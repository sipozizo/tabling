package sipozizo.tabling.domain.store.model.response;

import lombok.Builder;
import sipozizo.tabling.common.entity.Store;

import java.time.LocalTime;

@Builder
public record StoreResponse(
        Long id,
        String storeName,
        String storeNumber,
        String storeAddress,
        LocalTime openingTime,
        LocalTime closingTime,
        String category
) {
    public static StoreResponse fromEntity(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreNumber(),
                store.getStoreAddress(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getCategory()
        );
    }
}
