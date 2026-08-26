package br.com.lucas.alves.encurtador_url.controller.redirecionar;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.lucas.alves.encurtador_url.handlers.dto.ApiExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@ApiResponses(value = {
    @ApiResponse(responseCode = "404", description = "Código encurtado não encontrado", content = @Content(
        schema = @Schema(implementation = ApiExceptionResponse.class))),
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(
        schema = @Schema(implementation = ApiExceptionResponse.class)))
})
public interface IRedirecionarSwagger {

    @Operation(summary = "Redireciona para a URL original", operationId = "redirect", description = "Recebe um código encurtado e redireciona para a URL original correspondente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirecionamento bem-sucedido para a URL original")
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode);
}
