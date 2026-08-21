package br.com.lucas.alves.encurtador_url.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.lucas.alves.encurtador_url.exceptions.ShortCodeNotFoundException;
import br.com.lucas.alves.encurtador_url.handlers.dto.ApiExceptionResponse;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> shortCodeNotFound(ShortCodeNotFoundException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
            "URL não encontrada",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            null,
            null,
            getTimeStamp()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    private String getTimeStamp() {
        return java.time.ZonedDateTime.now().toString();
    }
}
