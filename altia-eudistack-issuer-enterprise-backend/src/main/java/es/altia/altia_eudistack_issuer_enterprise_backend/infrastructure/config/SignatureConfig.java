package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties.SignatureProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(SignatureProperties.class)
@RequiredArgsConstructor
public class SignatureConfig {

    private final YamlConfigAdapter configAdapter;
    private final SignatureProperties signatureProperties;

    public String getCoreDomain() {
        return configAdapter.getConfiguration(signatureProperties.coreUrl());
    }

    public String getProvider() {
        return configAdapter.getConfiguration(signatureProperties.provider());
    }
    
}
