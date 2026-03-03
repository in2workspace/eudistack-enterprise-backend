package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.issuer-core-backend")
@Validated
public record IssuerCoreBackendProperties(
        @NotBlank @URL String url
) {
}

