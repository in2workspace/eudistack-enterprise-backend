package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.ConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties.SignatureProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignatureConfig {

    private final ConfigAdapter configAdapter;
    private final SignatureProperties signatureProperties;

    public String getCoreDomain() {
        return configAdapter.getConfiguration(signatureProperties.coreUrl());
    }

    public String getProvider() {
        return configAdapter.getConfiguration(signatureProperties.provider());
    }
    
}
