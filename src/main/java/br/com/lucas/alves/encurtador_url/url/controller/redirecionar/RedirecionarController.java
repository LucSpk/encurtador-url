package br.com.lucas.alves.encurtador_url.url.controller.redirecionar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.lucas.alves.encurtador_url.url.application.service.RedirecionarService;

public class RedirecionarController implements IRedirecionarSwagger {

    private final RedirecionarService redirecionarService;

    public RedirecionarController(RedirecionarService redirecionarService) {
        this.redirecionarService = redirecionarService;
    }

    public ResponseEntity<Void> redirect(String shortCode) {
        String originalUrl = redirecionarService.redirecionar(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND.value()).header("Location", originalUrl).build();
    }
}
