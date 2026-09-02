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
import br.com.lucas.alves.encurtador_url.utils.CodificadorUtil;

@Service
public class UrlUseCase implements IUrlInputPort {
    private final String baseUrl;
    private final IUrlOutputPort urlRepository;

    public UrlUseCase(IUrlOutputPort urlRepository,  @Value("${app.shortener.base-url}") String baseUrl) {
        this.urlRepository = urlRepository;
        this.baseUrl = baseUrl;
    }
    
    @Transactional
    public EncurtarResponse encurtarUrl(EncurtarRequest request) {
        try {
            long id = urlRepository.saveUrl(request.getUrl(), null);

            String shortCode = CodificadorUtil.toBase62String(id);
            urlRepository.updateUrlShortCode(id, shortCode);

            return new EncurtarResponse(
                shortCode,
                baseUrl + shortCode
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