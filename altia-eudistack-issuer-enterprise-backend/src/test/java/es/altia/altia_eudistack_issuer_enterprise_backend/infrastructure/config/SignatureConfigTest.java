package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.SignatureProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SignatureConfigTest {

    private static final String CORE_URL = "https://issuer-core.example.com";
    private static final String PROVIDER = "remote-signature";

    @Mock
    private YamlConfigAdapter yamlConfigAdapter;

    @Spy
    private final SignatureProperties signatureProperties = new SignatureProperties(
            PROVIDER,
            CORE_URL
    );

    @InjectMocks
    private SignatureConfig signatureConfig;

    @Test
    void getCoreDomain_ValidCoreUrl_ReturnsResolvedCoreDomain() {
        when(yamlConfigAdapter.getConfiguration(CORE_URL))
                .thenReturn("https://resolved-core.example.com");

        String result = signatureConfig.getCoreDomain();

        assertThat(result).isEqualTo("https://resolved-core.example.com");
        verify(yamlConfigAdapter).getConfiguration(CORE_URL);
        verifyNoMoreInteractions(yamlConfigAdapter);
    }

    @Test
    void getProvider_ValidProvider_ReturnsResolvedProvider() {
        when(yamlConfigAdapter.getConfiguration(PROVIDER))
                .thenReturn("resolved-provider");

        String result = signatureConfig.getProvider();

        assertThat(result).isEqualTo("resolved-provider");
        verify(yamlConfigAdapter).getConfiguration(PROVIDER);
        verifyNoMoreInteractions(yamlConfigAdapter);
    }
}