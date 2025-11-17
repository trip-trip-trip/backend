package yeohaenggasijo.tripshot.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {
    private final EntityManager em;

    // EntityManager를 주입받아 QueryFactory 생성에 사용
    public QuerydslConfig(EntityManager em) {
        this.em = em;
    }

    // @Bean 어노테이션으로 Spring 컨테이너에 JPAQueryFactory 객체를 등록합니다.
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
