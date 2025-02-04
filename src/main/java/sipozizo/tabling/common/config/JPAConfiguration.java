package sipozizo.tabling.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 추후 QueryDSL로 변경 시 사용 예정
 */

//@Configuration
//public class JPAConfiguration {
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Bean
//    public JPAQueryFactory jpaQueryFactory() {
//        return new JPAQueryFactory(entityManager);
//    }
//}
