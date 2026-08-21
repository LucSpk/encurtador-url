package br.com.lucas.alves.encurtador_url.controller.url;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;

public interface IUrlSwagger {
    
    @PostMapping("/url")
    public ResponseEntity<EncurtarResponse> encurtarUrl(@RequestBody EncurtarRequest request);
}
