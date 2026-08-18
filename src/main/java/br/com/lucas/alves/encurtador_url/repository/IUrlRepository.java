package br.com.lucas.alves.encurtador_url.repository;

import br.com.lucas.alves.encurtador_url.domain.entity.Url;

public interface IUrlRepository {
    String getUrlByShortened(String shortened);
    Url getByUrl(String original);
    long saveUrl(String original, String shortened);
    void updateUrlShortCode(long id, String shortCode);
}