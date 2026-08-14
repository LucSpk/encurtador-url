package br.com.lucas.alves.encurtador_url.url.application.service.impl;

import br.com.lucas.alves.encurtador_url.url.application.dto.encurtar.*;

public class UrlService {
    private final String baseUrl = "http://localhost:8080/"; // Base URL for the shortened URLs
    public EncurtarResponse encurtarUrl(EncurtarRequest request) {
        // Implementation for shortening URL
        
        // TODO: Subistituir pelo retorno do banco de dados
        long id = System.currentTimeMillis(); // Placeholder for actual ID generation logic
        
        String shortCode = toBase62String(id); // Placeholder for actual short code generation logic

        return new EncurtarResponse(shortCode, baseUrl + shortCode);
    }

    private String toBase62String(long id) {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder shortCode = new StringBuilder();
        
        while (id > 0) {
            int remainder = (int) (id % 62);
            shortCode.append(characters.charAt(remainder));
            id /= 62;
        }
        
        return shortCode.reverse().toString(); 
    }
}