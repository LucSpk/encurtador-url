package br.com.lucas.alves.encurtador_url.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.lucas.alves.encurtador_url.application.ports.input.IRedirecionarInputPort;
import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;
import br.com.lucas.alves.encurtador_url.domain.exceptions.ShortCodeNotFoundException;

@Service
public class RedirecionarUseCase implements IRedirecionarInputPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirecionarUseCase.class);

    private final IUrlOutputPort urlRepository;
    
    public RedirecionarUseCase(IUrlOutputPort urlRepository) {
        this.urlRepository = urlRepository;
    }
    
    @Cacheable(value = "urls", key = "#shortCode")
    public String redirecionar(String shortCode) {
        if (shortCode == null || shortCode.trim().isEmpty()) {
            LOGGER.warn("Código curto inválido fornecido: {}", shortCode);
            throw new ShortCodeNotFoundException("URL não encontrada para o código encurtado fornecido.");
        }
        LOGGER.info("Consultando URL para código curto no banco de dados: {}", shortCode);
        String originalUrl = urlRepository.getUrlByShortened(shortCode);
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            throw new ShortCodeNotFoundException("URL não encontrada para o código encurtado fornecido.");
        }
        return originalUrl;
    }
}
