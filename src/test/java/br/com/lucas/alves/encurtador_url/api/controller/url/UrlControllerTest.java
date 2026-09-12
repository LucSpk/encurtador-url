package br.com.lucas.alves.encurtador_url.api.controller.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.lucas.alves.encurtador_url.api.responses.EncurtarResponse;
import br.com.lucas.alves.encurtador_url.api.requests.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.application.ports.input.IUrlInputPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UrlController")
class UrlControllerTest {

    @Mock 
    private IUrlInputPort urlInputPort;

    @InjectMocks 
    private UrlController urlController;

    @Test
    @DisplayName("Quando uma URL válida é fornecida, então retorna a URL encurtada com código de status 201")
    void whenValidUrlIsProvided_ThenReturnShortenedUrl_WithStatusCode201() {
        EncurtarResponse encurtarResponse = new EncurtarResponse("shortCode", "http://short.url/shortCode");
        EncurtarRequest request = new EncurtarRequest("https://www.example.com");
        
        //when(urlInputPort.encurtarUrl(any())).thenReturn(encurtarResponse);

        simulaContextoHttp();

        ResponseEntity<EncurtarResponse> response = urlController.encurtarUrl(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(encurtarResponse, response.getBody());
    }

    private void simulaContextoHttp() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setScheme("http");
        servletRequest.setServerName("localhost");
        servletRequest.setServerPort(8080);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));
    }
}
