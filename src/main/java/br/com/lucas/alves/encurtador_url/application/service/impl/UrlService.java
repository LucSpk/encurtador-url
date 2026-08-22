package br.com.lucas.alves.encurtador_url.application.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;
import br.com.lucas.alves.encurtador_url.application.service.IUrlService;
import br.com.lucas.alves.encurtador_url.domain.entity.Url;
import br.com.lucas.alves.encurtador_url.repository.IUrlRepository;
import br.com.lucas.alves.encurtador_url.utils.CodificadorUtil;

@Service
public class UrlService implements IUrlService {
    private final String baseUrl;
    private final IUrlRepository urlRepository;

    public UrlService(IUrlRepository urlRepository,  @Value("${app.shortener.base-url}") String baseUrl) {
        this.urlRepository = urlRepository;
        this.baseUrl = baseUrl;
    }
    
    public EncurtarResponse encurtarUrl(EncurtarRequest request) {
        try {
            long id = urlRepository.saveUrl(request.getUrl(), null);

            String shortCode = CodificadorUtil.toBase62String(id);
            urlRepository.updateUrlShortCode(id, shortCode);

            return new EncurtarResponse(
                shortCode,
                baseUrl + shortCode
            );

        } catch (DuplicateKeyException _) {     // Padrão sem exceção "_", pois o mesmo URL pode ser encurtado várias vezes
            @SuppressWarnings("java:S3655")
            Url url = urlRepository.getByUrl(request.getUrl()).get();

            return new EncurtarResponse(
                url.getShortCode(),
                baseUrl + url.getShortCode()
            );
        }
    }
}