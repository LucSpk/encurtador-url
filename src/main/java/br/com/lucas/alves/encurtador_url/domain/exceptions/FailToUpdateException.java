package br.com.lucas.alves.encurtador_url.domain.exceptions;

/**
 * FailToUpdateException
 */
public class FailToUpdateException  extends RuntimeException {
    public FailToUpdateException(String message) {
        super(message);
    }
}
