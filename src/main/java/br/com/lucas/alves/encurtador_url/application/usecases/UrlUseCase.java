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
import br.com.lucas.alves.encurtador_url.domain.exceptions.UrlNotFoundAfterDuplicateKeyException;

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
        if(request == null || request.getUrl() == null || request.getUrl().isEmpty()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }

        if(traceId == null || traceId.isEmpty()) {
            throw new IllegalArgumentException("Trace ID must not be null or empty");
        }
        
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
            Url url = urlRepository.getByUrl(request.getUrl())
                .orElseThrow(() -> new UrlNotFoundAfterDuplicateKeyException("URL not found after DuplicateKeyException"));

            return new EncurtarResponse(
                url.getShortCode(),
                baseUrl + url.getShortCode()
            );
        } catch(UrlNotFoundAfterDuplicateKeyException e) {
            throw e; 
        } catch (Exception e) {
            throw new RuntimeException("Error while shortening URL", e);
        }
    }
}