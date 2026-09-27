package br.com.lucas.alves.encurtador_url.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.com.lucas.alves.encurtador_url.application.ports.input.IRedirecionarInputPort;
import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;
import br.com.lucas.alves.encurtador_url.application.usecases.RedirecionarUseCase;

@SpringJUnitConfig(CacheIntegrationTest.TestCacheConfig.class)
class CacheIntegrationTest {

    @TestConfiguration
    @EnableCaching
    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("urls");
        }

        @Bean
        RedirecionarUseCase redirecionarUseCase(IUrlOutputPort urlOutputPort) {
            return new RedirecionarUseCase(urlOutputPort);
        }
    }

    @Autowired 
    private IRedirecionarInputPort redirecionarUseCase;

    @MockitoBean 
    private IUrlOutputPort urlOutputPort;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("urls").clear();
    }
    
    @Test
    @DisplayName("Deve buscar a URL no repository e armazená-la no cache")
    void deveArmazenarUrlNoCache() {
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

    @Test
    @DisplayName("Deve retornar a URL do cache sem consultar o repository novamente")
    void deveRetornarUrlDoCache() {

        String shortCode = "def456";
        String originalUrl = "https://google.com";

        when(urlOutputPort.getUrlByShortened(shortCode))
                .thenReturn(originalUrl);
        
        // Primeira chamada
        String firstResult = redirecionarUseCase.redirecionar(shortCode);

        // Segunda chamada
        String secondResult = redirecionarUseCase.redirecionar(shortCode);

        assertEquals(originalUrl, firstResult);
        assertEquals(originalUrl, secondResult);

        verify(urlOutputPort, times(1))
                .getUrlByShortened(shortCode);
    }
}
