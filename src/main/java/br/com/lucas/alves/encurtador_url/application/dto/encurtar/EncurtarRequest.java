package br.com.lucas.alves.encurtador_url.application.dto.encurtar;

public class EncurtarRequest {
    private final String url;

    public EncurtarRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }  
}
