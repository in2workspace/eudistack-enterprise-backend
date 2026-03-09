package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataAcquisitionProviderRegistryImplTest {

    @Test
    void shouldReturnProviderForRequestedType() {
        DataAcquisitionProvider provider = mock(DataAcquisitionProvider.class);
        when(provider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);

        DataAcquisitionProviderRegistryImpl registry =
                new DataAcquisitionProviderRegistryImpl(List.of(provider));

        DataAcquisitionProvider result = registry.get(DataAcquisitionProperties.SourceType.MOCK);

        assertThat(result).isSameAs(provider);
    }

    @Test
    void shouldThrowExceptionWhenProviderTypeIsNotFound() {
        DataAcquisitionProvider provider = mock(DataAcquisitionProvider.class);
        when(provider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);

        DataAcquisitionProviderRegistryImpl registry =
                new DataAcquisitionProviderRegistryImpl(List.of(provider));

        assertThatThrownBy(() -> registry.get(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No provider found for type: null");
    }

    @Test
    void shouldThrowExceptionWhenProviderTypeIsNotRegistered() {
        DataAcquisitionProvider provider = mock(DataAcquisitionProvider.class);

        DataAcquisitionProviderRegistryImpl registry =
                new DataAcquisitionProviderRegistryImpl(List.of(provider));

        assertThatThrownBy(() -> registry.get(DataAcquisitionProperties.SourceType.MOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No provider found for type: MOCK");
    }

    @Test
    void shouldThrowExceptionWhenTwoProvidersSupportTheSameType() {
        DataAcquisitionProvider firstProvider = mock(DataAcquisitionProvider.class);
        DataAcquisitionProvider secondProvider = mock(DataAcquisitionProvider.class);

        when(firstProvider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);
        when(secondProvider.getSupportedType()).thenReturn(DataAcquisitionProperties.SourceType.MOCK);

        assertThatThrownBy(() -> new DataAcquisitionProviderRegistryImpl(List.of(firstProvider, secondProvider)))
                .isInstanceOf(IllegalStateException.class);
    }
}