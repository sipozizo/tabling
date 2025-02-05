package sipozizo.tabling.domain.store.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.domain.store.model.dto.StoreDto;

@Getter
@AllArgsConstructor
public class StoreWithViewCountResponseV2 {

    private Long id;
    private String storeName;
    private String storeNumber;
    private Long viewCount;

    public static StoreWithViewCountResponseV2 of(StoreDto store, Long viewCount) {
        return new StoreWithViewCountResponseV2(store.getId(), store.getStoreName(), store.getStoreNumber(),viewCount);
    }
}
