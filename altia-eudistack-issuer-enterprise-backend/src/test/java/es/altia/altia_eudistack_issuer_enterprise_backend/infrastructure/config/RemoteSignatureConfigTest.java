package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties.RemoteSignatureProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

    private RemoteSignatureConfig remoteSignatureConfig;

    @Mock
    private YamlConfigAdapter yamlConfigAdapter;

    @BeforeEach
    void setUp() {

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
    void getRemoteSignatureDomain_ValidUrl_ReturnsResolvedDomain() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(URL)).thenReturn("https://resolved.example.com");

        // Act
        String result = remoteSignatureConfig.getRemoteSignatureDomain();

        // Assert
        assertThat(result).isEqualTo("https://resolved.example.com");
    }

    @Test
    void getRemoteSignatureSignPath_ValidSignPath_ReturnsResolvedSignPath() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(SIGN_PATH)).thenReturn("/resolved-sign");

        // Act
        String result = remoteSignatureConfig.getRemoteSignatureSignPath();

        // Assert
        assertThat(result).isEqualTo("/resolved-sign");
    }

    @Test
    void getRemoteSignatureClientId_ValidClientId_ReturnsResolvedClientId() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(CLIENT_ID)).thenReturn("resolved-client-id");

        // Act
        String result = remoteSignatureConfig.getRemoteSignatureClientId();

        // Assert
        assertThat(result).isEqualTo("resolved-client-id");
    }

    @Test
    void getRemoteSignatureClientSecret_ValidClientSecret_ReturnsResolvedClientSecret() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(CLIENT_SECRET)).thenReturn("resolved-client-secret");

        // Act
        String result = remoteSignatureConfig.getRemoteSignatureClientSecret();

        // Assert
        assertThat(result).isEqualTo("resolved-client-secret");
    }

    @Test
    void getRemoteSignatureCredentialId_ValidCredentialId_ReturnsResolvedCredentialId() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(CREDENTIAL_ID)).thenReturn("resolved-credential-id");

        // Act
        String result = remoteSignatureConfig.getRemoteSignatureCredentialId();

        // Assert
        assertThat(result).isEqualTo("resolved-credential-id");
    }

    @Test
    void getRemoteSignatureCredentialPassword_ValidCredentialPassword_ReturnsResolvedCredentialPassword() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(CREDENTIAL_PASSWORD)).thenReturn("resolved-credential-password");

        // Act
        String result = remoteSignatureConfig.getRemoteSignatureCredentialPassword();

        // Assert
        assertThat(result).isEqualTo("resolved-credential-password");
    }

    @Test
    void getRemoteSignatureType_ValidType_ReturnsResolvedType() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(TYPE)).thenReturn("resolved-type");

        // Act
        String result = remoteSignatureConfig.getRemoteSignatureType();

        // Assert
        assertThat(result).isEqualTo("resolved-type");
    }

    @Test
    void getCertificateInfoCacheTtl_ValidCacheTtl_ReturnsParsedDuration() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(CACHE_TTL)).thenReturn("PT30M");

        // Act
        Duration ttl = remoteSignatureConfig.getCertificateInfoCacheTtl();

        // Assert
        assertThat(ttl).isEqualTo(Duration.ofMinutes(30));
    }
}