package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import static org.assertj.core.api.Assertions.assertThat;

class DataAcquisitionSecurityConfigurationTest {

    private final DataAcquisitionSecurityConfiguration configuration =
            new DataAcquisitionSecurityConfiguration();

    @Test
    void DataAcquisitionAuthenticationConverter_ConfigurationIsInitialized_ReturnsConverter() {
        // Act
        DataAcquisitionAuthenticationConverter converter =
                configuration.dataAcquisitionAuthenticationConverter();

        // Assert
        assertThat(converter)
                .isInstanceOf(DataAcquisitionAuthenticationConverter.class);
    }

    @Test
    void DataAcquisitionIssuanceAuthenticationProvider_ConfigurationIsInitialized_ReturnsProvider() {
        // Act
        DataAcquisitionIssuanceAuthenticationProvider provider =
                configuration.dataAcquisitionIssuanceAuthenticationProvider();

        // Assert
        assertThat(provider)
                .isInstanceOf(DataAcquisitionIssuanceAuthenticationProvider.class);
    }

    @Test
    void DataAcquisitionIssuanceAuthenticationManager_ProviderIsProvided_ReturnsAuthenticationManager() {
        // Arrange
        DataAcquisitionIssuanceAuthenticationProvider provider =
                new DataAcquisitionIssuanceAuthenticationProvider();

        // Act
        AuthenticationManager manager =
                configuration.dataAcquisitionIssuanceAuthenticationManager(provider);

        // Assert
        assertThat(manager)
                .isInstanceOf(ProviderManager.class);
    }

    @Test
    void DataAcquisitionIssuanceAuthenticationFilter_ManagerAndConverterAreProvided_ReturnsAuthenticationFilter() {
        // Arrange
        AuthenticationManager manager =
                configuration.dataAcquisitionIssuanceAuthenticationManager(
                        new DataAcquisitionIssuanceAuthenticationProvider()
                );

        DataAcquisitionAuthenticationConverter converter =
                configuration.dataAcquisitionAuthenticationConverter();

        // Act
        DataAcquisitionAuthenticationFilter filter =
                configuration.dataAcquisitionIssuanceAuthenticationFilter(manager, converter);

        // Assert
        assertThat(filter)
                .isInstanceOf(DataAcquisitionAuthenticationFilter.class);
    }
}