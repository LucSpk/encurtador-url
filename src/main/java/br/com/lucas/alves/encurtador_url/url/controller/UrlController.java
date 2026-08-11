package br.com.lucas.alves.encurtador_url.url.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.lucas.alves.encurtador_url.url.application.dto.encurtar.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.url.application.dto.encurtar.EncurtarResponse;

@RestController
public class UrlController implements IUrlSwagger {
    public ResponseEntity<EncurtarResponse> encurtarUrl(@RequestBody EncurtarRequest request) {
        // TODO: Implementar a lógica para encurtar a URL recebida no request
        return ResponseEntity.ok(null);
    }
}
