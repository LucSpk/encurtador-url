package br.com.lucas.alves.encurtador_url.domain.exceptions;

public class UrlNotFoundAfterDuplicateKeyException extends RuntimeException {
    public UrlNotFoundAfterDuplicateKeyException(String message) {
        super(message);
    }
}
