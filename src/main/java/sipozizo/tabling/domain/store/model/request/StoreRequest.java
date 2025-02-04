package sipozizo.tabling.domain.store.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;

public record StoreRequest(
        @NotBlank(message = "가게명은 필수 입력값입니다.")
        String storeName,

        @Pattern(
                regexp = "^(01[0-9])(\\d{3,4})(\\d{4})$",
                message = "연락처는 숫자만 입력할 수 있습니다."
        )
        String storeNumber,

        @NotBlank(message = "주소는 필수 입력값입니다.")
        String storeAddress,

        @Pattern(
                regexp = "^\\d{4}-\\d{2}-\\d{2}$",
                message = "사업자 번호는 필수 입력값입니다. 0000-00-00(예시) 형식으로 입력해주세요."
        )
        String registrationNumber,

        @JsonFormat(pattern = "HH:mm:ss")
        @NotNull(message = "가게 개시시간은 필수 입력값입니다. 00:00:00 형식으로 입력해주세요.")
        LocalTime openingTime,

        @JsonFormat(pattern = "HH:mm:ss")
        @NotNull(message = "가게 마감시간은 필수 입력값입니다. 00:00:00 형식으로 입력해주세요.")
        LocalTime closingTime,

        @NotBlank(message = "가게 카테고리는 필수 입력값입니다.")
        String category,

        @NotBlank(message = "입장 가능 숫자를 입력하십시오.")
        Integer maxSeatingCapacity
) {
}
