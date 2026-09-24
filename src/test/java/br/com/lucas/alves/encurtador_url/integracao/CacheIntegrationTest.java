package br.com.lucas.alves.encurtador_url.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;

import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;
import br.com.lucas.alves.encurtador_url.application.usecases.RedirecionarUseCase;

@SpringBootTest
class CacheIntegrationTest {

    static GenericContainer<?> redis =
        new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    static {
        redis.start();
    }

    @Autowired
    private RedirecionarUseCase redirecionarUseCase;

    @MockitoBean
    private IUrlOutputPort urlOutputPort;

    @Autowired
    private CacheManager cacheManager;

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add(
            "spring.data.redis.host",
            redis::getHost
        );

        registry.add(
            "spring.data.redis.port",
            () -> redis.getMappedPort(6379)
        );
    }

    @AfterAll
    static void tearDown() {
        redis.stop();
    }

    @BeforeEach
    void setUp() {
        cacheManager.getCache("urls").clear();
    }
    
    @Test
    @DisplayName("Deve buscar a URL no repository e armazená-la no Redis")
    void deveArmazenarUrlNoRedis() {
        String shortCode = "abc123";
        String originalUrl = "https://google.com";

        when(urlOutputPort.getUrlByShortened(shortCode))
                .thenReturn(originalUrl);

        String result = redirecionarUseCase.redirecionar(shortCode);
        assertEquals(originalUrl, result);
        verify(urlOutputPort, times(1))
                .getUrlByShortened(shortCode);

        Object cachedValue = cacheManager
            .getCache("urls")
            .get(shortCode)
            .get();
        
        assertEquals(originalUrl, cachedValue);
    }
}
