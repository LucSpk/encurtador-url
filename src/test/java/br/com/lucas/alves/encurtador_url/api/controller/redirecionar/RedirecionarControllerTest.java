package br.com.lucas.alves.encurtador_url.api.controller.redirecionar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import br.com.lucas.alves.encurtador_url.application.ports.input.IRedirecionarInputPort;

@DisplayName("Testes para RedirecionarController")
class RedirecionarControllerTest {
    private RedirecionarController redirecionarController;
    
    @Test
    @DisplayName("Quando uma URL curta válida é fornecida, então redireciona para a URL original com código de status 302")
    void whenValidShortUrlIsProvided_ThenRedirectToOriginalUrl_WithStatusCode302() {
        IRedirecionarInputPort redirecionarInputPort = Mockito.mock(IRedirecionarInputPort.class);
        Mockito.when(redirecionarInputPort.redirecionar(Mockito.anyString())).thenReturn("https://www.example.com");
    
        redirecionarController = new RedirecionarController(redirecionarInputPort);

        ResponseEntity<Void> response = redirecionarController.redirect("shortCode");
        
        Assertions.assertEquals(302, response.getStatusCode().value());
        Assertions.assertEquals("https://www.example.com", response.getHeaders().getLocation().toString());
    }
}
