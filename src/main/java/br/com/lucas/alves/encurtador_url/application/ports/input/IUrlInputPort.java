package br.com.lucas.alves.encurtador_url.application.ports.input;

import br.com.lucas.alves.encurtador_url.api.requests.EncurtarRequest;
import br.com.lucas.alves.encurtador_url.api.responses.EncurtarResponse;

public interface IUrlInputPort {
    public EncurtarResponse encurtarUrl(EncurtarRequest request, String traceId);
}