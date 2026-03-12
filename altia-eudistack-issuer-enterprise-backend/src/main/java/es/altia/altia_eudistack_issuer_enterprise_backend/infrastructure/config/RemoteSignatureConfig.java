package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.RemoteSignatureProperties;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RemoteSignatureConfig {

    private final YamlConfigAdapter configAdapter;
    private final RemoteSignatureProperties remoteSignatureProperties;

    public RemoteSignatureConfig(YamlConfigAdapter yamlConfigAdapter, RemoteSignatureProperties remoteSignatureProperties) {
        this.configAdapter = yamlConfigAdapter;
        this.remoteSignatureProperties = remoteSignatureProperties;
    }

    public String getRemoteSignatureDomain() {
        return configAdapter.getConfiguration(remoteSignatureProperties.url());
    }

    public String getRemoteSignatureSignPath() {
        return configAdapter.getConfiguration(remoteSignatureProperties.paths().signPath());
    }

    public String getRemoteSignatureClientId() {
        return configAdapter.getConfiguration(remoteSignatureProperties.clientId());
    }

    public String getRemoteSignatureClientSecret() {
        return configAdapter.getConfiguration(remoteSignatureProperties.clientSecret());
    }

    public String getRemoteSignatureCredentialId() {
        return configAdapter.getConfiguration(remoteSignatureProperties.credentialId());
    }

    public String getRemoteSignatureCredentialPassword() {
        return configAdapter.getConfiguration(remoteSignatureProperties.credentialPassword());
    }

    public String getRemoteSignatureType() {
        return configAdapter.getConfiguration(remoteSignatureProperties.type());
    }

    public Duration getCertificateInfoCacheTtl() {
        String raw = configAdapter.getConfiguration(remoteSignatureProperties.certificateInfoCacheTtl());
        return DurationStyle.detectAndParse(raw);
    }
    
}
