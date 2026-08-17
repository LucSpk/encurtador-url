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
        long id = urlRepository.saveUrl(request.getUrl(), null);

        String shortCode = CodificadorUtil.toBase62String(id);
        urlRepository.updateUrlShortCode(id, shortCode);

        return new EncurtarResponse(shortCode, baseUrl + shortCode);
    }
}