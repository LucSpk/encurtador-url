package br.com.lucas.alves.encurtador_url.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import br.com.lucas.alves.encurtador_url.api.requests.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.api.responses.EncurtarResponse;
import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;
import br.com.lucas.alves.encurtador_url.domain.entity.Url;

@ExtendWith (MockitoExtension.class)
@DisplayName("Testes para UrlUseCase")
class UrlUseCaseTest {
    @Mock 
    private IUrlOutputPort urlRepository;

    private UrlUseCase urlUseCase;

    @BeforeEach 
    void setUp() {
        urlUseCase = new UrlUseCase(urlRepository, "http://localhost:8080/");
    }

    @Nested 
    @DisplayName("Testes para o método encurtarUrl")
    class EncurtarUrlTests {
        
        @Test
        @DisplayName ("Quando uma URL válida é fornecida, então retorna a resposta de encurtamento")
        void whenValidUrlIsProvided_ThenReturnsShortenedUrl() {
            when(urlRepository.saveUrl("https://www.example.com", "123456")).thenReturn(1L);
            
            EncurtarRequest request = new EncurtarRequest("https://www.example.com");
            EncurtarResponse response = urlUseCase.encurtarUrl(request, "123456");

            System.out.println("Response: " + response.getShortCode() + ", " + response.getShortUrl());

            assertNotNull(response);
            assertEquals("123456", response.getShortCode());
            assertEquals("http://localhost:8080/123456", response.getShortUrl());

            verify(urlRepository, times(1)).saveUrl("https://www.example.com", "123456");
        }

        @Test
        @DisplayName ("Quando uma URL duplicada é fornecida, então retorna a resposta de encurtamento existente")
        void whenDuplicateUrlIsProvided_ThenReturnsExistingShortenedUrl() {
            when(urlRepository.saveUrl("https://www.example.com", "123456")).thenThrow(new DuplicateKeyException("Duplicate key"));

            Url existingUrl = new Url();
            existingUrl.setShortCode("123456");
            existingUrl.setOriginalUrl("https://www.example.com");
            existingUrl.setCreatedAt("2023-01-01T00:00:00Z");

            when(urlRepository.getByUrl("https://www.example.com")).thenReturn(Optional.of(existingUrl));
            
            EncurtarRequest request = new EncurtarRequest("https://www.example.com");
            EncurtarResponse response = urlUseCase.encurtarUrl(request, "123456");

            assertNotNull(response);
            assertEquals("123456", response.getShortCode());
            assertEquals("http://localhost:8080/123456", response.getShortUrl());

            verify(urlRepository, times(1)).saveUrl("https://www.example.com", "123456");
            verify(urlRepository, times(1)).getByUrl("https://www.example.com");
        }

        @Test
        @DisplayName("Quando a URL base não está configurada, então lança uma exceção")
        void whenBaseUrlIsNotConfigured_ThenThrowsException() {
            UrlUseCase useCaseWithoutBaseUrl = new UrlUseCase(urlRepository, "");
            EncurtarRequest request = new EncurtarRequest("https://www.example.com");

            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> useCaseWithoutBaseUrl.encurtarUrl(request, "123456")
            );

            assertEquals("Base URL is not configured.", exception.getMessage());
            verify(urlRepository, times(0)).saveUrl("https://www.example.com", "123456");
        }

        @Test
        @DisplayName("Quando a URL base é nula, então lança uma exceção")
        void whenBaseUrlIsNull_ThenThrowsException() {
            UrlUseCase useCaseWithoutBaseUrl = new UrlUseCase(urlRepository, null);
            EncurtarRequest request = new EncurtarRequest("https://www.example.com");

            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> useCaseWithoutBaseUrl.encurtarUrl(request, "123456")
            );

            assertEquals("Base URL is not configured.", exception.getMessage());
            verify(urlRepository, times(0)).saveUrl("https://www.example.com", "123456");
        }

        @Test 
        @DisplayName("Quando tenta salvar recebe uma excption qualquer e retorna uma RuntimeException")
        void whenSaveThrowsAnyException_ThenThrowsRuntimeException() {
            when(urlRepository.saveUrl("https://www.example.com", "123456")).thenThrow(new RuntimeException("Database error"));
            
            EncurtarRequest request = new EncurtarRequest("https://www.example.com");
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> urlUseCase.encurtarUrl(request, "123456")
            );

            assertEquals("Error while shortening URL", exception.getMessage());
            verify(urlRepository, times(1)).saveUrl("https://www.example.com", "123456");
        }
    }
}
