package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties.SignatureProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignatureConfigTest {

    private static final String CORE_URL = "https://issuer-core.example.com";
    private static final String PROVIDER = "remote-signature";

    private SignatureConfig signatureConfig;

    @Mock
    private YamlConfigAdapter yamlConfigAdapter;

    @BeforeEach
    void setUp() {

        SignatureProperties signatureProperties = new SignatureProperties(
                PROVIDER,
                CORE_URL
        );

        signatureConfig = new SignatureConfig(yamlConfigAdapter, signatureProperties);
    }

    @Test
    void getCoreDomain_ValidCoreUrl_ReturnsResolvedCoreDomain() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(CORE_URL))
                .thenReturn("https://resolved-core.example.com");

        // Act
        String result = signatureConfig.getCoreDomain();

        // Assert
        assertThat(result).isEqualTo("https://resolved-core.example.com");
        verify(yamlConfigAdapter).getConfiguration(CORE_URL);
        verifyNoMoreInteractions(yamlConfigAdapter);
    }

    @Test
    void getProvider_ValidProvider_ReturnsResolvedProvider() {
        // Arrange
        when(yamlConfigAdapter.getConfiguration(PROVIDER))
                .thenReturn("resolved-provider");

        // Act
        String result = signatureConfig.getProvider();

        // Assert
        assertThat(result).isEqualTo("resolved-provider");
        verify(yamlConfigAdapter).getConfiguration(PROVIDER);
        verifyNoMoreInteractions(yamlConfigAdapter);
    }
}