package br.com.lucas.alves.encurtador_url.application.service.impl;

import org.springframework.stereotype.Service;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;
import br.com.lucas.alves.encurtador_url.application.service.IUrlService;
import br.com.lucas.alves.encurtador_url.repository.IUrlRepository;
import br.com.lucas.alves.encurtador_url.utils.CodificadorUtil;

@Service
public class UrlService implements IUrlService {
    private static final String baseUrl = "http://localhost:8080/"; // Base URL for the shortened URLs
    
    private final IUrlRepository urlRepository;
    
    public UrlService(IUrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }
    
    public EncurtarResponse encurtarUrl(EncurtarRequest request) {
        // Implementation for shortening URL
        
        // TODO: Subistituir pelo retorno do banco de dados
        long id = System.currentTimeMillis(); // Placeholder for actual ID generation logic
        
        String shortCode = CodificadorUtil.toBase62String(id); // Placeholder for actual short code generation logic
        urlRepository.saveUrl(request.getUrl(), shortCode);

        return new EncurtarResponse(shortCode, baseUrl + shortCode);
    }
}