package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties(prefix = "signing")
@Validated
public record SignatureProperties(
        @NotBlank String provider,
        @NotBlank @URL String coreUrl
) {

    @ConstructorBinding
    public SignatureProperties(String provider, String coreUrl) {
        this.provider = provider;
        this.coreUrl = coreUrl;
    }

}