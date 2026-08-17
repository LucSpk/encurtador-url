package br.com.lucas.alves.encurtador_url.controller.redirecionar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.lucas.alves.encurtador_url.application.service.IRedirecionarService;

@RestController
public class RedirecionarController implements IRedirecionarSwagger {

    private final IRedirecionarService redirecionarService;

    public RedirecionarController(IRedirecionarService redirecionarService) {
        this.redirecionarService = redirecionarService;
    }

    public ResponseEntity<Void> redirect(String shortCode) {
        String originalUrl = redirecionarService.redirecionar(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND.value()).header("Location", originalUrl).build();
    }
}
