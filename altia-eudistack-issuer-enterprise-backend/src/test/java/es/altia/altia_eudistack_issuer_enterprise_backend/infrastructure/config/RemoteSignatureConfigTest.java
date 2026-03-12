package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.RemoteSignatureProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoteSignatureConfigTest {

    private static final String TYPE = "server";
    private static final String URL = "https://remote-signature.example.com";
    private static final String SIGN_PATH = "/signature/sign";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_SECRET = "client-secret";
    private static final String CREDENTIAL_ID = "credential-id";
    private static final String CREDENTIAL_PASSWORD = "credential-password";
    private static final String CACHE_TTL = "PT15M";

    @Mock
    private YamlConfigAdapter yamlConfigAdapter;

    @Spy
    private final RemoteSignatureProperties remoteSignatureProperties = new RemoteSignatureProperties(
            TYPE,
            URL,
            new RemoteSignatureProperties.Paths(SIGN_PATH),
            CLIENT_ID,
            CLIENT_SECRET,
            CREDENTIAL_ID,
            CREDENTIAL_PASSWORD,
            CACHE_TTL
    );

    @InjectMocks
    private RemoteSignatureConfig remoteSignatureConfig;

    @Test
    void getRemoteSignatureDomain_ValidUrl_ReturnsResolvedDomain() {
        when(yamlConfigAdapter.getConfiguration(URL)).thenReturn("https://resolved.example.com");

        String result = remoteSignatureConfig.getRemoteSignatureDomain();

        assertThat(result).isEqualTo("https://resolved.example.com");
    }

    @Test
    void getRemoteSignatureSignPath_ValidSignPath_ReturnsResolvedSignPath() {
        when(yamlConfigAdapter.getConfiguration(SIGN_PATH)).thenReturn("/resolved-sign");

        String result = remoteSignatureConfig.getRemoteSignatureSignPath();

        assertThat(result).isEqualTo("/resolved-sign");
    }

    @Test
    void getRemoteSignatureClientId_ValidClientId_ReturnsResolvedClientId() {
        when(yamlConfigAdapter.getConfiguration(CLIENT_ID)).thenReturn("resolved-client-id");

        String result = remoteSignatureConfig.getRemoteSignatureClientId();

        assertThat(result).isEqualTo("resolved-client-id");
    }

    @Test
    void getRemoteSignatureClientSecret_ValidClientSecret_ReturnsResolvedClientSecret() {
        when(yamlConfigAdapter.getConfiguration(CLIENT_SECRET)).thenReturn("resolved-client-secret");

        String result = remoteSignatureConfig.getRemoteSignatureClientSecret();

        assertThat(result).isEqualTo("resolved-client-secret");
    }

    @Test
    void getRemoteSignatureCredentialId_ValidCredentialId_ReturnsResolvedCredentialId() {
        when(yamlConfigAdapter.getConfiguration(CREDENTIAL_ID)).thenReturn("resolved-credential-id");

        String result = remoteSignatureConfig.getRemoteSignatureCredentialId();

        assertThat(result).isEqualTo("resolved-credential-id");
    }

    @Test
    void getRemoteSignatureCredentialPassword_ValidCredentialPassword_ReturnsResolvedCredentialPassword() {
        when(yamlConfigAdapter.getConfiguration(CREDENTIAL_PASSWORD)).thenReturn("resolved-credential-password");

        String result = remoteSignatureConfig.getRemoteSignatureCredentialPassword();

        assertThat(result).isEqualTo("resolved-credential-password");
    }

    @Test
    void getRemoteSignatureType_ValidType_ReturnsResolvedType() {
        when(yamlConfigAdapter.getConfiguration(TYPE)).thenReturn("resolved-type");

        String result = remoteSignatureConfig.getRemoteSignatureType();

        assertThat(result).isEqualTo("resolved-type");
    }

    @Test
    void getCertificateInfoCacheTtl_ValidCacheTtl_ReturnsParsedDuration() {
        when(yamlConfigAdapter.getConfiguration(CACHE_TTL)).thenReturn("PT30M");

        Duration ttl = remoteSignatureConfig.getCertificateInfoCacheTtl();

        assertThat(ttl).isEqualTo(Duration.ofMinutes(30));
    }
}