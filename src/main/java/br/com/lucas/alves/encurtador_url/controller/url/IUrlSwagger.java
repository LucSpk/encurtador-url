package br.com.lucas.alves.encurtador_url.controller.url;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;
import br.com.lucas.alves.encurtador_url.handlers.dto.ApiExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@ApiResponses(value = {
    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
        schema = @Schema(implementation = ApiExceptionResponse.class))),
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(
        schema = @Schema(implementation = ApiExceptionResponse.class)))
    }
)
public interface IUrlSwagger {
    
    @Operation(summary = "Encurta uma URL", operationId = "encurtarUrl", description = "Recebe uma URL original e retorna uma URL encurtada correspondente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "URL encurtada com sucesso", content = @Content(
            schema = @Schema(implementation = EncurtarResponse.class)))
    })
    @PostMapping("/url")
    public ResponseEntity<EncurtarResponse> encurtarUrl(@Valid @RequestBody EncurtarRequest request);
}
