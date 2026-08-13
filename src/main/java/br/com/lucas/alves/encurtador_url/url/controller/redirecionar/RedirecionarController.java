package br.com.lucas.alves.encurtador_url.url.controller.redirecionar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RedirecionarController implements IRedirecionarSwagger {
    public ResponseEntity<Void> redirect(String shortCode) {
        // Implementar a lógica para redirecionar para a URL original com base no shortCode recebido
        return ResponseEntity.status(HttpStatus.FOUND.value()).build();
    }
}
