package br.com.lucas.alves.encurtador_url.application.service.impl;

import org.springframework.stereotype.Service;

import br.com.lucas.alves.encurtador_url.application.service.IRedirecionarService;
import br.com.lucas.alves.encurtador_url.repository.IUrlRepository;

@Service
public class RedirecionarService implements IRedirecionarService {

    private final IUrlRepository urlRepository;
    
    public RedirecionarService(IUrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }
   
    public String redirecionar(String shortCode) {
        return urlRepository.getUrlByShortened(shortCode);
    }
}
