package br.com.lucas.alves.encurtador_url.application.service.impl;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;
import br.com.lucas.alves.encurtador_url.utils.CodificadorUtil;

public class UrlService {
    private final String baseUrl = "http://localhost:8080/"; // Base URL for the shortened URLs
    public EncurtarResponse encurtarUrl(EncurtarRequest request) {
        // Implementation for shortening URL
        
        // TODO: Subistituir pelo retorno do banco de dados
        long id = System.currentTimeMillis(); // Placeholder for actual ID generation logic
        
        String shortCode = CodificadorUtil.toBase62String(id); // Placeholder for actual short code generation logic

        return new EncurtarResponse(shortCode, baseUrl + shortCode);
    }
}