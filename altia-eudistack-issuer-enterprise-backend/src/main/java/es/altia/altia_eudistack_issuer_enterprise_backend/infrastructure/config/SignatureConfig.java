package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.SignatureProperties;
import org.springframework.stereotype.Component;


@Component
public class SignatureConfig {

    private final YamlConfigAdapter configAdapter;
    private final SignatureProperties signatureProperties;

    public SignatureConfig(YamlConfigAdapter yamlConfigAdapter, SignatureProperties signatureProperties) {
        this.configAdapter = yamlConfigAdapter;
        this.signatureProperties = signatureProperties;
    }

    public String getCoreDomain() {
        return configAdapter.getConfiguration(signatureProperties.coreUrl());
    }

    public String getProvider() {
        return configAdapter.getConfiguration(signatureProperties.provider());
    }
    
}
