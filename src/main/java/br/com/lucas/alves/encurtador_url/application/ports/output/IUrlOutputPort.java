package br.com.lucas.alves.encurtador_url.application.ports.output;

import java.util.Optional;

import br.com.lucas.alves.encurtador_url.domain.entity.Url;

public interface IUrlOutputPort {
    String getUrlByShortened(String shortened);
    Optional<Url> getByUrl(String original);
    void updateUrlShortCode(long id, String shortCode);
    long saveUrl(String original, String shortened);
    long saveUrlWithTtl(String original, String shortened, Integer ttlMinutes);
    String getUrlByShortCodeWithExpiryCheck(String shortCode);
}