package br.com.lucas.alves.encurtador_url.application.usecases;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lucas.alves.encurtador_url.api.requests.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.api.responses.EncurtarResponse;
import br.com.lucas.alves.encurtador_url.application.ports.input.IUrlInputPort;
import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;
import br.com.lucas.alves.encurtador_url.domain.entity.Url;

@Service
public class UrlUseCase implements IUrlInputPort {
    private final String baseUrl;
    private final IUrlOutputPort urlRepository;

    public UrlUseCase(IUrlOutputPort urlRepository,  @Value("${app.shortener.base-url}") String baseUrl) {
        this.urlRepository = urlRepository;
        this.baseUrl = baseUrl;
    }
    
    @Transactional
    public EncurtarResponse encurtarUrl(EncurtarRequest request, String traceId) {
        if(baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("Base URL is not configured.");
        }
        
        try {
            urlRepository.saveUrl(request.getUrl(), traceId);

            return new EncurtarResponse(
                traceId,
                baseUrl + traceId
            );
        } catch (DuplicateKeyException e) {
            @SuppressWarnings("java:S3655")
            Url url = urlRepository.getByUrl(request.getUrl()).get();

            return new EncurtarResponse(
                url.getShortCode(),
                baseUrl + url.getShortCode()
            );
        }
    }
}