package sipozizo.tabling.store.model.request;

import java.sql.Time;

public record StoreRequest(
        String storeName,
        String storeNumber,
        String storeAddress,
        String registrationNumber,
        String category,
        Time openingTime,
        Time closingTime
) {
}
