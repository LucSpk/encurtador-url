package br.com.lucas.alves.encurtador_url.api.controller.redirecionar;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import br.com.lucas.alves.encurtador_url.application.ports.input.IRedirecionarInputPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RedirecionarController")
class RedirecionarControllerTest {
    @Mock 
    private IRedirecionarInputPort redirecionarInputPort;

    @InjectMocks 
    private RedirecionarController redirecionarController;
    
    @Test
    @DisplayName("Quando uma URL curta válida é fornecida, então redireciona para a URL original com código de status 302")
    void whenValidShortUrlIsProvided_ThenRedirectToOriginalUrl_WithStatusCode302() {
        when(redirecionarInputPort.redirecionar(anyString())).thenReturn("https://www.example.com");
    
        ResponseEntity<Void> response = redirecionarController.redirect("shortCode");
        
        Assertions.assertEquals(302, response.getStatusCode().value());
        Assertions.assertEquals("https://www.example.com", response.getHeaders().getLocation().toString());
    }
}
