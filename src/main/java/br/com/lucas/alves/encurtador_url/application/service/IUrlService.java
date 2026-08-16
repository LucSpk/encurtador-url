package br.com.lucas.alves.encurtador_url.application.service;

import br.com.lucas.alves.encurtador_url.application.dto.encurtar.*;

public interface IUrlService {
    public EncurtarResponse encurtarUrl(EncurtarRequest request);
}