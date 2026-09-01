package br.com.lucas.alves.encurtador_url.application.service;

import br.com.lucas.alves.encurtador_url.api.requests.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.api.responses.EncurtarResponse;

public interface IUrlService {
    public EncurtarResponse encurtarUrl(EncurtarRequest request);
}