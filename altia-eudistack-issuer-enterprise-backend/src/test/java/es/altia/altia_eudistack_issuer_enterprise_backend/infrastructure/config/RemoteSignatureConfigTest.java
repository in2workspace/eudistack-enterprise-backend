package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties.RemoteSignatureProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RemoteSignatureConfigTest {

    private static final String TYPE = "server";
    private static final String URL = "https://remote-signature.example.com";
    private static final String SIGN_PATH = "/signature/sign";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_SECRET = "client-secret";
    private static final String CREDENTIAL_ID = "credential-id";
    private static final String CREDENTIAL_PASSWORD = "credential-password";
    private static final String CACHE_TTL = "PT15M";

    private YamlConfigAdapter yamlConfigAdapter;
    private RemoteSignatureConfig remoteSignatureConfig;

    @BeforeEach
    void setUp() {
        yamlConfigAdapter = mock(YamlConfigAdapter.class);

        RemoteSignatureProperties remoteSignatureProperties = new RemoteSignatureProperties(
                TYPE,
                URL,
                new RemoteSignatureProperties.Paths(SIGN_PATH),
                CLIENT_ID,
                CLIENT_SECRET,
                CREDENTIAL_ID,
                CREDENTIAL_PASSWORD,
                CACHE_TTL
        );

        remoteSignatureConfig = new RemoteSignatureConfig(yamlConfigAdapter, remoteSignatureProperties);
    }

    @Test
    void shouldReturnResolvedRemoteSignatureConfigurationValues() {
        given(yamlConfigAdapter.getConfiguration(URL)).willReturn("https://resolved.example.com");
        given(yamlConfigAdapter.getConfiguration(SIGN_PATH)).willReturn("/resolved-sign");
        given(yamlConfigAdapter.getConfiguration(CLIENT_ID)).willReturn("resolved-client-id");
        given(yamlConfigAdapter.getConfiguration(CLIENT_SECRET)).willReturn("resolved-client-secret");
        given(yamlConfigAdapter.getConfiguration(CREDENTIAL_ID)).willReturn("resolved-credential-id");
        given(yamlConfigAdapter.getConfiguration(CREDENTIAL_PASSWORD)).willReturn("resolved-credential-password");
        given(yamlConfigAdapter.getConfiguration(TYPE)).willReturn("resolved-type");

        assertThat(remoteSignatureConfig.getRemoteSignatureDomain()).isEqualTo("https://resolved.example.com");
        assertThat(remoteSignatureConfig.getRemoteSignatureSignPath()).isEqualTo("/resolved-sign");
        assertThat(remoteSignatureConfig.getRemoteSignatureClientId()).isEqualTo("resolved-client-id");
        assertThat(remoteSignatureConfig.getRemoteSignatureClientSecret()).isEqualTo("resolved-client-secret");
        assertThat(remoteSignatureConfig.getRemoteSignatureCredentialId()).isEqualTo("resolved-credential-id");
        assertThat(remoteSignatureConfig.getRemoteSignatureCredentialPassword()).isEqualTo("resolved-credential-password");
        assertThat(remoteSignatureConfig.getRemoteSignatureType()).isEqualTo("resolved-type");

        verify(yamlConfigAdapter).getConfiguration(URL);
        verify(yamlConfigAdapter).getConfiguration(SIGN_PATH);
        verify(yamlConfigAdapter).getConfiguration(CLIENT_ID);
        verify(yamlConfigAdapter).getConfiguration(CLIENT_SECRET);
        verify(yamlConfigAdapter).getConfiguration(CREDENTIAL_ID);
        verify(yamlConfigAdapter).getConfiguration(CREDENTIAL_PASSWORD);
        verify(yamlConfigAdapter).getConfiguration(TYPE);
    }

    @Test
    void shouldReturnParsedCertificateInfoCacheTtl() {
        given(yamlConfigAdapter.getConfiguration(CACHE_TTL)).willReturn("PT30M");

        Duration ttl = remoteSignatureConfig.getCertificateInfoCacheTtl();

        assertThat(ttl).isEqualTo(Duration.ofMinutes(30));

        verify(yamlConfigAdapter).getConfiguration(CACHE_TTL);
    }
}