package br.com.lucas.alves.encurtador_url.repository;

public interface IUrlRepository {
    String getUrlByShortened(String shortened);
    long saveUrl(String original, String shortened);
    void updateUrlShortCode(long id, String shortCode);
}