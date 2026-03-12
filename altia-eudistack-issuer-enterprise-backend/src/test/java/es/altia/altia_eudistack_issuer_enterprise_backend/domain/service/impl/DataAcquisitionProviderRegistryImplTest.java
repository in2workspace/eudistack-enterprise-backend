package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataAcquisitionProviderRegistryImplTest {

    @Mock
    private DataAcquisitionProvider provider;

    @Mock
    private DataAcquisitionProvider secondProvider;

    private DataAcquisitionProviderRegistryImpl registry;

    @BeforeEach
    void setUp() {
        when(provider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);
        registry = new DataAcquisitionProviderRegistryImpl(List.of(provider));
    }

    @Test
    void Get_ProviderExistsForRequestedType_ReturnsProvider() {
        // Act
        DataAcquisitionProvider result = registry.get(DataAcquisitionProperties.SourceType.MOCK);

        // Assert
        assertThat(result).isSameAs(provider);
    }

    @Test
    void Get_ProviderTypeIsNull_ThrowsIllegalStateException() {
        // Act & Assert
        assertThatThrownBy(() -> registry.get(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No provider found for type: null");
    }

    @Test
    void Get_ProviderTypeIsNotRegistered_ThrowsIllegalStateException() {
        // Arrange
        DataAcquisitionProviderRegistryImpl emptyRegistry =
                new DataAcquisitionProviderRegistryImpl(List.of());

        // Act & Assert
        assertThatThrownBy(() -> emptyRegistry.get(DataAcquisitionProperties.SourceType.MOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No provider found for type: MOCK");
    }

    @Test
    void Constructor_TwoProvidersSupportTheSameType_ThrowsIllegalStateException() {
        // Arrange
        when(provider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);
        when(secondProvider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);
        List<DataAcquisitionProvider> providers = List.of(provider, secondProvider);

        // Act & Assert
        assertThatThrownBy(() -> new DataAcquisitionProviderRegistryImpl(providers))
                .isInstanceOf(IllegalStateException.class);
    }
}