package br.com.lucas.alves.encurtador_url.controller.url;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.application.dto.encurtar.EncurtarResponse;
import br.com.lucas.alves.encurtador_url.application.service.IUrlService;

@RestController
public class UrlController implements IUrlSwagger {

    private final IUrlService urlService;

    public UrlController(IUrlService urlService) {
        this.urlService = urlService;
    }

    public ResponseEntity<EncurtarResponse> encurtarUrl(@RequestBody EncurtarRequest request) {
        EncurtarResponse response = urlService.encurtarUrl(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/{id}")
            .buildAndExpand(response.getShortCode())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
