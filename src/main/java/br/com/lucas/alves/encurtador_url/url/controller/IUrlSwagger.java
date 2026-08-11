package br.com.lucas.alves.encurtador_url.url.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lucas.alves.encurtador_url.url.application.dto.encurtador.*;

public interface IUrlSwagger {
    
    @PostMapping("/encurtar")
    public ResponseEntity<EncurtarResponse> encurtarUrl(@RequestBody EncurtarRequest request);
}
