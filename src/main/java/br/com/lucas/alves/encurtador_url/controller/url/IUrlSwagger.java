package br.com.lucas.alves.encurtador_url.controller.url;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requisição inválida"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
})
public interface IUrlSwagger {
    
    
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "URL encurtada com sucesso")
    })
    @PostMapping("/url")
    public ResponseEntity<EncurtarResponse> encurtarUrl(@Valid @RequestBody EncurtarRequest request);
}
