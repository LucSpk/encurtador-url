package br.com.lucas.alves.encurtador_url.integracao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.lucas.alves.encurtador_url.api.requests.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.api.responses.EncurtarResponse;
import br.com.lucas.alves.encurtador_url.api.controller.redirecionar.RedirecionarController;
import br.com.lucas.alves.encurtador_url.api.controller.url.UrlController;
import br.com.lucas.alves.encurtador_url.application.ports.input.IRedirecionarInputPort;
import br.com.lucas.alves.encurtador_url.application.ports.input.IUrlInputPort;
import br.com.lucas.alves.encurtador_url.domain.exceptions.ShortCodeNotFoundException;

@WebMvcTest(controllers = {UrlController.class, RedirecionarController.class})
@Import(ApiIntegrationTest.TestCacheConfig.class)
@DisplayName("Testes de integração da API")
class ApiIntegrationTest {

    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("urls");
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUrlInputPort urlInputPort;

    @MockitoBean
    private IRedirecionarInputPort redirecionarInputPort;

    @Test
    @DisplayName("Deve criar uma URL curta e responder com 201 e Location")
    void deveCriarUrlCurta() throws Exception {
        EncurtarResponse response = new EncurtarResponse("abc123", "http://localhost/abc123");
        when(urlInputPort.encurtarUrl(any(EncurtarRequest.class), eq("trace-test")))
            .thenReturn(response);

        mockMvc.perform(post("/url")
                .header("traceId", "trace-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://example.com\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/abc123"))
            .andExpect(jsonPath("$.shortCode").value("abc123"))
            .andExpect(jsonPath("$.shortUrl").value("http://localhost/abc123"));

        verify(urlInputPort).encurtarUrl(any(EncurtarRequest.class), eq("trace-test"));
    }

    @Test
    @DisplayName("Deve redirecionar para a URL original com 302")
    void deveRedirecionarParaUrlOriginal() throws Exception {
        when(redirecionarInputPort.redirecionar("abc123"))
            .thenReturn("https://example.com");

        mockMvc.perform(get("/abc123"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "https://example.com"));

        verify(redirecionarInputPort).redirecionar("abc123");
    }

    @Test
    @DisplayName("Deve responder 404 padronizado para código inexistente")
    void deveRetornar404ParaCodigoInexistente() throws Exception {
        when(redirecionarInputPort.redirecionar("missing"))
            .thenThrow(new ShortCodeNotFoundException("Código não encontrado"));

        mockMvc.perform(get("/missing"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.friendlyMessage").value("URL não encontrada"))
            .andExpect(jsonPath("$.technicalMessage").value("Código não encontrado"))
            .andExpect(jsonPath("$.errorCode").value(404))
            .andExpect(jsonPath("$.path").value("/missing"));
    }
}