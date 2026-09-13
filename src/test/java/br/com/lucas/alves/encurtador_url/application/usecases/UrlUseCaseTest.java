package br.com.lucas.alves.encurtador_url.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.lucas.alves.encurtador_url.api.requests.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.api.responses.EncurtarResponse;
import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;

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
    }
}
