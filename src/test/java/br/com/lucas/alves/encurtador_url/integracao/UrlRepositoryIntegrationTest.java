package br.com.lucas.alves.encurtador_url.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.com.lucas.alves.encurtador_url.infrastructure.postgre.UrlRepository;

@SpringBootTest
@Testcontainers
class UrlRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("url_shortener")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("db/schema.sql");

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM urls");
    }

    @Test
    void deveSalvarEConsultarUrlNoPostgres() {
        long id = urlRepository.saveUrl(
            "https://www.exemplo.com",
            "abc123"
        );

        assertEquals(
            "https://www.exemplo.com",
            urlRepository.getUrlByShortened("abc123")
        );
    }
}
