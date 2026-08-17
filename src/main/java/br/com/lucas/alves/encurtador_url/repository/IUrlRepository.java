package br.com.lucas.alves.encurtador_url.repository;

public interface IUrlRepository {
    String getUrlByShortened(String shortened);
    void saveUrl(String original, String shortened);
}