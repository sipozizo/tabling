package sipozizo.tabling.domain.store.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sipozizo.tabling.common.entity.Store;

@Getter
@AllArgsConstructor
public class StoreWithViewCountResponseV1 {

    private Long id;
    private String storeName;
    private String storeNumber;
    private Integer viewCount;

    public static StoreWithViewCountResponseV1 of(Store store) {
        return new StoreWithViewCountResponseV1(store.getId(), store.getStoreName(), store.getStoreNumber(), store.getView());
    }
}
