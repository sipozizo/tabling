package sipozizo.tabling.domain.store.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sipozizo.tabling.domain.store.repository.StoreRepository;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {
    private StoreRepository storeRepository;
    //private UserRepository userRepository;

    
}
