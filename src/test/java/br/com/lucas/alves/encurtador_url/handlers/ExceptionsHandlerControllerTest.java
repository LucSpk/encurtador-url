package br.com.lucas.alves.encurtador_url.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToInsertException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToRetrieveGeneratedIdException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.ShortCodeNotFoundException;
import br.com.lucas.alves.encurtador_url.handlers.dto.ApiExceptionResponse;

@DisplayName("Testes para ExceptionsHandlerController")
class ExceptionsHandlerControllerTest {

    private ExceptionsHandlerController controller;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        controller = new ExceptionsHandlerController();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/short/abc123");
        MDC.put("traceId", "trace-123");
    }

    @AfterEach
    void tearDown() {
        MDC.remove("traceId");
    }

    @Test
    @DisplayName("Quando a URL não é encontrada, então retorna 404 com mensagem padronizada")
    void whenShortCodeNotFound_ThenReturnsNotFoundResponse() {
        ShortCodeNotFoundException exception = new ShortCodeNotFoundException("Código não encontrado");

        ResponseEntity<ApiExceptionResponse> response = controller.shortCodeNotFound(exception, request);

        assertEquals(404, response.getStatusCode().value());
        assertApiExceptionResponse(response, "URL não encontrada", "Código não encontrado", 404);
    }

    @Test
    @DisplayName("Quando falha ao inserir a URL, então retorna 400 com mensagem padronizada")
    void whenFailToInsert_ThenReturnsBadRequestResponse() {
        FailToInsertException exception = new FailToInsertException("Erro ao salvar URL no banco");

        ResponseEntity<ApiExceptionResponse> response = controller.failToInsert(exception, request);

        assertEquals(400, response.getStatusCode().value());
        assertApiExceptionResponse(response, "Falha ao inserir URL", "Erro ao salvar URL no banco", 400);
    }

    @Test
    @DisplayName("Quando falha ao recuperar o ID gerado, então retorna 400 com mensagem padronizada")
    void whenFailToRetrieveGeneratedId_ThenReturnsBadRequestResponse() {
        FailToRetrieveGeneratedIdException exception = new FailToRetrieveGeneratedIdException("ID gerado não foi encontrado");

        ResponseEntity<ApiExceptionResponse> response = controller.failToRetrieveGeneratedId(exception, request);

        assertEquals(400, response.getStatusCode().value());
        assertApiExceptionResponse(response, "Falha ao recuperar ID gerado", "ID gerado não foi encontrado", 400);
    }

    @Test
    @DisplayName("Quando ocorre uma exceção inesperada, então retorna 500 com mensagem padronizada")
    void whenUnexpectedError_ThenReturnsInternalServerErrorResponse() {
        RuntimeException exception = new RuntimeException("Erro inesperado no sistema");

        ResponseEntity<ApiExceptionResponse> response = controller.unexpectedError(exception, request);

        assertEquals(500, response.getStatusCode().value());
        assertApiExceptionResponse(response, "Uma falha Inesperada Aconteceu", "Erro inesperado no sistema", 500);
    }

    private void assertApiExceptionResponse(ResponseEntity<ApiExceptionResponse> response, String expectedFriendlyMessage,
            String expectedTechnicalMessage, int expectedStatusCode) {
        assertNotNull(response.getBody());
        ApiExceptionResponse body = response.getBody();

        assertEquals(expectedFriendlyMessage, body.getFriendlyMessage());
        assertEquals(expectedTechnicalMessage, body.getTechnicalMessage());
        assertEquals(expectedStatusCode, body.getErrorCode());
        assertEquals("/api/short/abc123", body.getPath());
        assertEquals("trace-123", body.getTraceId());
        assertNotNull(body.getTimestamp());
        assertNotNull(body.getDetails());
        assertTrue(body.getDetails().isEmpty());
    }
}
