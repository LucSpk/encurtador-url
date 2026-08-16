package br.com.lucas.alves.encurtador_url.application.dto.encurtar;

public class EncurtarResponse {
    private final String shortCode;
    private final String shortUrl;

    public EncurtarResponse(String shortCode, String shortUrl) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
