package br.com.lucas.alves.encurtador_url.api.requests;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Min;
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
    @Min(1)
    private final Integer ttl;

    public EncurtarRequest(String url, Integer ttl) {
        this.url = url;
        this.ttl = ttl;
    }

    public String getUrl() {
        return url;
    }

    public Integer getTtlMinutes() {
        return ttl;
    }
}
