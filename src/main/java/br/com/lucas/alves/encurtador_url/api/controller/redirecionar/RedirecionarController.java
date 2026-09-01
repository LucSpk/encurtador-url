package br.com.lucas.alves.encurtador_url.api.controller.redirecionar;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.lucas.alves.encurtador_url.application.ports.input.IRedirecionarInputPort;

@RestController
public class RedirecionarController implements IRedirecionarSwagger {

    private final IRedirecionarInputPort redirecionarService;

    public RedirecionarController(IRedirecionarInputPort redirecionarService) {
        this.redirecionarService = redirecionarService;
    }

    public ResponseEntity<Void> redirect(String shortCode) {
        String originalUrl = redirecionarService.redirecionar(shortCode);

        URI uri = URI.create(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND.value()).location(uri).build();
    }
}
