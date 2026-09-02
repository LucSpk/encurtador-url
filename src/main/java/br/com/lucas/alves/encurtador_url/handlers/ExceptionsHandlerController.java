package br.com.lucas.alves.encurtador_url.handlers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToInsertException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToRetrieveGeneratedIdException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.ShortCodeNotFoundException;
import br.com.lucas.alves.encurtador_url.handlers.dto.ApiExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionsHandlerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionsHandlerController.class);

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> shortCodeNotFound(ShortCodeNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        
        ApiExceptionResponse response = buildApiExceptionResponse(
            "URL não encontrada", 
            ex.getMessage(), 
            status.value(), 
            new HashMap<>(), 
            request);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(FailToInsertException.class)
    public ResponseEntity<ApiExceptionResponse> failToInsert(FailToInsertException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        ApiExceptionResponse response = buildApiExceptionResponse(
            "Falha ao inserir URL", 
            ex.getMessage(), 
            status.value(), 
            new HashMap<>(), 
            request);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(FailToRetrieveGeneratedIdException.class)
    public ResponseEntity<ApiExceptionResponse> failToRetrieveGeneratedId(FailToRetrieveGeneratedIdException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        ApiExceptionResponse response = buildApiExceptionResponse(
            "Falha ao recuperar ID gerado", 
            ex.getMessage(), 
            status.value(), 
            new HashMap<>(), 
            request);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> unexpectedError(FailToRetrieveGeneratedIdException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        ApiExceptionResponse response = buildApiExceptionResponse(
            "Uma falha Inesperada Aconteceu", 
            ex.getMessage(), 
            status.value(), 
            new HashMap<>(), 
            request);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Cria uma resposta padronizada para exceções da API, incluindo informações
     * sobre o erro, a requisição HTTP e o rastreamento da execução.
     *
     * <p>O método também registra os detalhes do erro no log da aplicação,
     * utilizando o identificador de rastreamento armazenado no MDC, quando disponível.</p>
     *
     * @param error      descrição ou tipo do erro ocorrido
     * @param message    mensagem detalhada associada ao erro
     * @param statusCode código HTTP correspondente ao erro
     * @param details    informações adicionais relacionadas ao erro
     * @param request    requisição HTTP que originou o erro
     * @return uma instância de {@link ApiExceptionResponse} contendo os dados
     *         padronizados da exceção
     */
    private ApiExceptionResponse buildApiExceptionResponse(String error, String message, int statusCode, Map<String, Object> details, HttpServletRequest request) {
        
        String traceId = MDC.get("traceId");
        String path = request.getRequestURI();
        String timestamp = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toString();

        LOGGER.error("Error occurred: {}, message: {}, statusCode: {}, details: {}, path: {}, traceId: {}, timestamp: {}", error, message, statusCode, details, path, traceId, timestamp);

        return new ApiExceptionResponse(
            error,
            message,
            statusCode,
            details,
            path,
            traceId,
            timestamp
        );
    }
}
