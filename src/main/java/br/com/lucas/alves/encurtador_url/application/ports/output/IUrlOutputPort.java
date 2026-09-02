package br.com.lucas.alves.encurtador_url.application.ports.output;

import java.util.Optional;

import br.com.lucas.alves.encurtador_url.domain.entity.Url;

public interface IUrlOutputPort {
    String getUrlByShortened(String shortened);
    Optional<Url> getByUrl(String original);
    long saveUrl(String original, String shortened);
    void updateUrlShortCode(long id, String shortCode);
}