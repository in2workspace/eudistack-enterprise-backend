package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import static org.assertj.core.api.Assertions.assertThat;

class DataAcquisitionSecurityConfigurationTest {

    private final DataAcquisitionSecurityConfiguration configuration =
            new DataAcquisitionSecurityConfiguration();

    @Test
    void shouldCreateDataAcquisitionAuthenticationConverter() {
        DataAcquisitionAuthenticationConverter converter =
                configuration.dataAcquisitionAuthenticationConverter();

        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(DataAcquisitionAuthenticationConverter.class);
    }

    @Test
    void shouldCreateDataAcquisitionIssuanceAuthenticationProvider() {
        DataAcquisitionIssuanceAuthenticationProvider provider =
                configuration.dataAcquisitionIssuanceAuthenticationProvider();

        assertThat(provider).isNotNull();
        assertThat(provider).isInstanceOf(DataAcquisitionIssuanceAuthenticationProvider.class);
    }

    @Test
    void shouldCreateDataAcquisitionIssuanceAuthenticationManager() {
        DataAcquisitionIssuanceAuthenticationProvider provider =
                new DataAcquisitionIssuanceAuthenticationProvider();

        AuthenticationManager manager =
                configuration.dataAcquisitionIssuanceAuthenticationManager(provider);

        assertThat(manager).isNotNull();
        assertThat(manager).isInstanceOf(ProviderManager.class);
    }

    @Test
    void shouldCreateDataAcquisitionIssuanceAuthenticationFilter() {
        AuthenticationManager manager =
                configuration.dataAcquisitionIssuanceAuthenticationManager(
                        new DataAcquisitionIssuanceAuthenticationProvider()
                );
        DataAcquisitionAuthenticationConverter converter =
                configuration.dataAcquisitionAuthenticationConverter();

        DataAcquisitionAuthenticationFilter filter =
                configuration.dataAcquisitionIssuanceAuthenticationFilter(manager, converter);

        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(DataAcquisitionAuthenticationFilter.class);
    }
}
