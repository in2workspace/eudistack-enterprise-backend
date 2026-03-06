package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl.YamlConfigAdapter;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties.SignatureProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class SignatureConfigTest {

    private static final String CORE_URL = "https://issuer-core.example.com";
    private static final String PROVIDER = "remote-signature";

    private YamlConfigAdapter yamlConfigAdapter;
    private SignatureConfig signatureConfig;

    @BeforeEach
    void setUp() {
        yamlConfigAdapter = mock(YamlConfigAdapter.class);

        SignatureProperties signatureProperties = new SignatureProperties(
                PROVIDER,
                CORE_URL
        );

        signatureConfig = new SignatureConfig(yamlConfigAdapter, signatureProperties);
    }

    @Test
    void shouldReturnResolvedCoreDomain() {
        given(yamlConfigAdapter.getConfiguration(CORE_URL))
                .willReturn("https://resolved-core.example.com");

        String result = signatureConfig.getCoreDomain();

        assertThat(result).isEqualTo("https://resolved-core.example.com");

        verify(yamlConfigAdapter).getConfiguration(CORE_URL);
        verifyNoMoreInteractions(yamlConfigAdapter);
    }

    @Test
    void shouldReturnResolvedProvider() {
        given(yamlConfigAdapter.getConfiguration(PROVIDER))
                .willReturn("resolved-provider");

        String result = signatureConfig.getProvider();

        assertThat(result).isEqualTo("resolved-provider");

        verify(yamlConfigAdapter).getConfiguration(PROVIDER);
        verifyNoMoreInteractions(yamlConfigAdapter);
    }
}