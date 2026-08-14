package br.com.lucas.alves.encurtador_url.url.controller.url;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.lucas.alves.encurtador_url.url.application.dto.encurtar.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.url.application.dto.encurtar.EncurtarResponse;
import br.com.lucas.alves.encurtador_url.url.application.service.IUrlService;

@RestController
public class UrlController implements IUrlSwagger {

    private final IUrlService urlService;

    public UrlController(IUrlService urlService) {
        this.urlService = urlService;
    }

    public ResponseEntity<EncurtarResponse> encurtarUrl(@RequestBody EncurtarRequest request) {
        EncurtarResponse response = urlService.encurtarUrl(request);
        return ResponseEntity.ok(response);
    }
}
