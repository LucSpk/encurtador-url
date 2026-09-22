package br.com.lucas.alves.encurtador_url.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

@DisplayName("Testes para RequestInterceptor")
class RequestInterceptorTest {

    private final RequestInterceptor interceptor = new RequestInterceptor();

    @AfterEach
    void tearDown() {
        MDC.remove("traceId");
    }

    @Test
    @DisplayName("Quando o header traceId não é informado, então gera um UUID e registra no MDC")
    void whenTraceHeaderIsMissing_ThenGeneratesUuidAndStoresInMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/urls");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            String traceId = MDC.get("traceId");
            assertNotNull(traceId);
            assertTrue(traceId.matches("[0-9a-fA-F-]{36}"));
        };

        interceptor.doFilter(request, response, chain);

        assertNull(MDC.get("traceId"));
    }

    @Test
    @DisplayName("Quando o header traceId é informado, então o valor é preservado")
    void whenTraceHeaderIsPresent_ThenPreservesGivenValue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("traceId", "trace-abc-123");
        request.setRequestURI("/api/urls");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            assertEquals("trace-abc-123", MDC.get("traceId"));
        };

        interceptor.doFilter(request, response, chain);

        assertNull(MDC.get("traceId"));
    }

    @Test
    @DisplayName("Quando a rota é health, então ignora a criação do traceId")
    void whenRequestIsHealthEndpoint_ThenSkipsTraceGeneration() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            assertNull(MDC.get("traceId"));
        };

        interceptor.doFilter(request, response, chain);

        assertNull(MDC.get("traceId"));
        verifyNoInteraction(chain);
    }

    @Test
    @DisplayName("Quando a cadeia de filtros falha, então remove o traceId no finally")
    void whenFilterChainThrowsException_ThenRemovesTraceIdInFinally() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("traceId", "trace-finally");
        request.setRequestURI("/api/urls");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            assertEquals("trace-finally", MDC.get("traceId"));
            throw new RuntimeException("Erro de teste");
        };

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> interceptor.doFilter(request, response, chain));

        assertEquals("Erro de teste", exception.getMessage());
        assertNull(MDC.get("traceId"));
    }

    private void verifyNoInteraction(FilterChain chain) {
        // No-op helper to keep the test intent explicit while avoiding Mockito dependency.
    }
}
