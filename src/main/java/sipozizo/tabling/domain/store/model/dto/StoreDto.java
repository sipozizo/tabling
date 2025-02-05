package sipozizo.tabling.domain.store.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sipozizo.tabling.common.entity.Store;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto implements Serializable {
    private Long id;
    private String storeName;
    private String storeNumber;
    private String storeAddress;
    private String registrationNumber;
    private String openingTime;
    private String closingTime;
    private String category;

    public static StoreDto of(Store store) {
        return new StoreDto(
                store.getId(),
                store.getStoreName(),
                store.getStoreNumber(),
                store.getStoreAddress(),
                store.getRegistrationNumber(),
                store.getOpeningTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                store.getClosingTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                store.getCategory()
        );
    }
}
