package br.com.lucas.alves.encurtador_url.api.requests;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EncurtarRequest {
    @NotBlank(message = "A URL não pode ser nula ou vazia.")
    @URL
    @Pattern(
        regexp = "^https?://.*$",
        message = "A URL deve usar http ou https"
    )
    private final String url;

    public EncurtarRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }  
}
