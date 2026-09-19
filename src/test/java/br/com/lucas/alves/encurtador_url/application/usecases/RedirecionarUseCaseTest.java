package br.com.lucas.alves.encurtador_url.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RedirecionarUseCase")
class RedirecionarUseCaseTest {

    @Mock
    private IUrlOutputPort urlRepository;

    @InjectMocks
    private RedirecionarUseCase redirecionarUseCase;

    @Test
    @DisplayName("Quando um código curto válido é fornecido, então retorna a URL original")
    void whenValidShortCodeIsProvided_ThenReturnOriginalUrl() {
        String shortCode = "shortCode";
        String originalUrl = "https://www.example.com";

        when(urlRepository.getUrlByShortened(shortCode)).thenReturn(originalUrl);

        String result = redirecionarUseCase.redirecionar(shortCode);

        assertEquals(originalUrl, result);
        verify(urlRepository).getUrlByShortened(shortCode);
    }

    @Test 
    @DisplayName("Quando um código curto inválido é fornecido, então retorna null")
    void whenInvalidShortCodeIsProvided_ThenReturnNull() {
        String shortCode = "invalidShortCode";

        when(urlRepository.getUrlByShortened(shortCode)).thenReturn(null);

        String result = redirecionarUseCase.redirecionar(shortCode);

        assertEquals(null, result);
        verify(urlRepository).getUrlByShortened(shortCode);
    }
}
