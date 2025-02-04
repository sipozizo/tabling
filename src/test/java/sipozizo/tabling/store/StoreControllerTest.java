//package sipozizo.tabling.store;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.BDDMockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import sipozizo.tabling.domain.store.controller.StoreController;
//import sipozizo.tabling.domain.store.model.request.StoreRequest;
//import sipozizo.tabling.domain.store.model.response.StoreResponse;
//import sipozizo.tabling.domain.store.service.StoreService;
//
//import java.time.LocalTime;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(StoreController.class)
//class StoreControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private StoreService storeService;
//
//    @Test
//    @DisplayName("가게 생성 API 테스트")
//    @WithMockUser
//    void createStoreTest() throws Exception {
//        // given
//        StoreRequest request = new StoreRequest(
//                "병천이네고등어백반", "010-1234-5678", "사랑시 고백구 행복동",
//                "0000-00-0000", LocalTime.of(9, 0), LocalTime.of(22, 0), "한식"
//        );
//
//        doNothing().when(storeService).createStore(any(StoreRequest.class));
//
//        // when & then
//        mockMvc.perform(post("/api/v1/stores")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("가게 단건 조회 API 테스트")
//    @WithMockUser
//    void getStoreTest() throws Exception {
//        // given
//        Long storeId = 1L;
//        StoreResponse response = new StoreResponse(storeId, "Test Store", "010-1234-5678",
//                "사랑시 고백구 행복동", LocalTime.of(9, 0), LocalTime.of(22, 0), "한식");
//
//        BDDMockito.given(storeService.getStoreByIdV2(storeId)).willReturn(response);
//
//        // when & then
//        mockMvc.perform(get("/api/v1/stores/{storeId}", storeId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(storeId))
//                .andExpect(jsonPath("$.storeName").value("병천이네 고등어 백반"))
//                .andExpect(jsonPath("$.category").value("한식"));
//    }
//}
