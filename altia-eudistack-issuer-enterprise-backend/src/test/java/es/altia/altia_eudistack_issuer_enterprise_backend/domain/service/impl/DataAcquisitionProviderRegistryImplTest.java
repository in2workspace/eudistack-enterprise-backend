package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataAcquisitionProviderRegistryImplTest {

    @Test
    void Get_ProviderExistsForRequestedType_ReturnsProvider() {
        // Arrange
        DataAcquisitionProvider provider = mock(DataAcquisitionProvider.class);
        when(provider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);

        DataAcquisitionProviderRegistryImpl registry =
                new DataAcquisitionProviderRegistryImpl(List.of(provider));

        // Act
        DataAcquisitionProvider result = registry.get(DataAcquisitionProperties.SourceType.MOCK);

        // Assert
        assertThat(result).isSameAs(provider);
    }

    @Test
    void Get_ProviderTypeIsNull_ThrowsIllegalStateException() {
        // Arrange
        DataAcquisitionProvider provider = mock(DataAcquisitionProvider.class);
        when(provider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);

        DataAcquisitionProviderRegistryImpl registry =
                new DataAcquisitionProviderRegistryImpl(List.of(provider));

        // Act & Assert
        assertThatThrownBy(() -> registry.get(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No provider found for type: null");
    }

    @Test
    void Get_ProviderTypeIsNotRegistered_ThrowsIllegalStateException() {
        // Arrange
        DataAcquisitionProvider provider = mock(DataAcquisitionProvider.class);

        DataAcquisitionProviderRegistryImpl registry =
                new DataAcquisitionProviderRegistryImpl(List.of(provider));

        // Act & Assert
        assertThatThrownBy(() -> registry.get(DataAcquisitionProperties.SourceType.MOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No provider found for type: MOCK");
    }

    @Test
    void Constructor_TwoProvidersSupportTheSameType_ThrowsIllegalStateException() {
        // Arrange
        DataAcquisitionProvider firstProvider = mock(DataAcquisitionProvider.class);
        DataAcquisitionProvider secondProvider = mock(DataAcquisitionProvider.class);

        when(firstProvider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);
        when(secondProvider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);

        // Act & Assert
        assertThatThrownBy(() -> new DataAcquisitionProviderRegistryImpl(List.of(firstProvider, secondProvider)))
                .isInstanceOf(IllegalStateException.class);
    }
}