package br.com.lucas.alves.encurtador_url.controller.url;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;
import jakarta.validation.Valid;

public interface IUrlSwagger {
    
    @PostMapping("/url")
    public ResponseEntity<EncurtarResponse> encurtarUrl(@Valid @RequestBody EncurtarRequest request);
}
