package br.com.lucas.alves.encurtador_url.application.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.lucas.alves.encurtador_url.application.service.IRedirecionarService;
import br.com.lucas.alves.encurtador_url.repository.IUrlRepository;

@Service
public class RedirecionarService implements IRedirecionarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirecionarService.class);

    private final IUrlRepository urlRepository;
    
    public RedirecionarService(IUrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }
    
    @Cacheable(value = "urls", key = "#shortCode")
    public String redirecionar(String shortCode) {
        LOGGER.info("Consultando URL para código curto no banco de dados: {}", shortCode);
        return urlRepository.getUrlByShortened(shortCode);
    }
}
