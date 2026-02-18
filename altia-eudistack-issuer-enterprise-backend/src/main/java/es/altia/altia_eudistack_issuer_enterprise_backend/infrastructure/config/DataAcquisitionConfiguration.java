package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataAcquisitionConfiguration {

    private final DataAcquisitionProperties properties;

    @Bean
    public Map<String, DataAcquisitionProperties.Source> sourcesByCredentialConfigurationId() {

        if (properties.sources() == null || properties.sources().isEmpty()) {
            log.error("No data acquisition sources configured");
            throw new IllegalStateException("No data acquisition sources configured");
        }

        Map<String, DataAcquisitionProperties.Source> sourcesByCredentialConfigurationId =
                properties.sources().stream()
                        .collect(Collectors.toMap(
                                DataAcquisitionProperties.Source::credentialConfigurationId,
                                Function.identity(),
                                (existing, _) -> {
                                    String credentialConfigurationId = existing.credentialConfigurationId();
                                    String errorMessage = "Duplicate credentialConfigurationId '" + credentialConfigurationId
                                            + "' detected in data acquisition configuration; please check your YAML/properties configuration for duplicate entries.";
                                    log.error(errorMessage);
                                    throw new IllegalStateException(errorMessage);
                                }
                        ));

        log.debug("Configured {} data acquisition source(s)", sourcesByCredentialConfigurationId.size());

        return sourcesByCredentialConfigurationId;
    }
}
