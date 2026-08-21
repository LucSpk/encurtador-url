package br.com.lucas.alves.encurtador_url.exceptions;

public class ShortCodeNotFoundException extends RuntimeException {
    public ShortCodeNotFoundException(String message) {
        super(message);
    }
}
