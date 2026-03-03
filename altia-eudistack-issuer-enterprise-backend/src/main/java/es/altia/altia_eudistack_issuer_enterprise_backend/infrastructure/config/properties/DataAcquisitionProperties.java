package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "app.data-acquisition")
public record DataAcquisitionProperties(
        List<Source> sources
) {

    public record Source(
            String credentialConfigurationId,
            SourceType type,
            String endpoint,
            String baseDn,
            String bindDn,
            String bindPassword,
            String subjectIdentifierAttribute,
            Mapping mapping,
            Duration timeout,
            Retry retry
    ) {
    }

    public record Retry(
            int maxAttempts,
            Duration backoff
    ) {
    }

    public record Mapping(
            String firstName,
            String familyName,
            String email,
            String employeeId
    ) {
    }

    public enum SourceType {
        MOCK
    }
}
