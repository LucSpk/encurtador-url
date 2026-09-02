package br.com.lucas.alves.encurtador_url.domain.exceptions;

public class FailToInsertException extends RuntimeException {
    public FailToInsertException(String message) {
        super(message);
    }
}
