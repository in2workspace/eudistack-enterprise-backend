package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties.RemoteSignatureProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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
    void shouldReturnResolvedRemoteSignatureDomain() {
        given(yamlConfigAdapter.getConfiguration(URL)).willReturn("https://resolved.example.com");

        String result = remoteSignatureConfig.getRemoteSignatureDomain();

        assertThat(result).isEqualTo("https://resolved.example.com");
    }

    @Test
    void shouldReturnResolvedRemoteSignatureSignPath() {
        given(yamlConfigAdapter.getConfiguration(SIGN_PATH)).willReturn("/resolved-sign");

        String result = remoteSignatureConfig.getRemoteSignatureSignPath();

        assertThat(result).isEqualTo("/resolved-sign");
    }

    @Test
    void shouldReturnResolvedRemoteSignatureClientId() {
        given(yamlConfigAdapter.getConfiguration(CLIENT_ID)).willReturn("resolved-client-id");

        String result = remoteSignatureConfig.getRemoteSignatureClientId();

        assertThat(result).isEqualTo("resolved-client-id");
    }

    @Test
    void shouldReturnResolvedRemoteSignatureClientSecret() {
        given(yamlConfigAdapter.getConfiguration(CLIENT_SECRET)).willReturn("resolved-client-secret");

        String result = remoteSignatureConfig.getRemoteSignatureClientSecret();

        assertThat(result).isEqualTo("resolved-client-secret");
    }

    @Test
    void shouldReturnResolvedRemoteSignatureCredentialId() {
        given(yamlConfigAdapter.getConfiguration(CREDENTIAL_ID)).willReturn("resolved-credential-id");

        String result = remoteSignatureConfig.getRemoteSignatureCredentialId();

        assertThat(result).isEqualTo("resolved-credential-id");
    }

    @Test
    void shouldReturnResolvedRemoteSignatureCredentialPassword() {
        given(yamlConfigAdapter.getConfiguration(CREDENTIAL_PASSWORD)).willReturn("resolved-credential-password");

        String result = remoteSignatureConfig.getRemoteSignatureCredentialPassword();

        assertThat(result).isEqualTo("resolved-credential-password");
    }

    @Test
    void shouldReturnResolvedRemoteSignatureType() {
        given(yamlConfigAdapter.getConfiguration(TYPE)).willReturn("resolved-type");

        String result = remoteSignatureConfig.getRemoteSignatureType();

        assertThat(result).isEqualTo("resolved-type");
    }

    @Test
    void shouldReturnParsedCertificateInfoCacheTtl() {
        given(yamlConfigAdapter.getConfiguration(CACHE_TTL)).willReturn("PT30M");

        Duration ttl = remoteSignatureConfig.getCertificateInfoCacheTtl();

        assertThat(ttl).isEqualTo(Duration.ofMinutes(30));
    }
}