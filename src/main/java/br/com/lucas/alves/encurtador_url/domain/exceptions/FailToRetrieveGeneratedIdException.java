package br.com.lucas.alves.encurtador_url.domain.exceptions;

public class FailToRetrieveGeneratedIdException extends RuntimeException {
    public FailToRetrieveGeneratedIdException(String message) {
        super(message);
    }
}
