package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.DataAcquisitionProviderRegistry;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataAcquisitionProviderRegistryImpl implements DataAcquisitionProviderRegistry {
    private final Map<DataAcquisitionProperties.SourceType,
            DataAcquisitionProvider> providers;

    public DataAcquisitionProviderRegistryImpl(List<DataAcquisitionProvider> providerList) {
        super();
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        DataAcquisitionProvider::getSupportedType,
                        Function.identity()
                ));
    }

    public DataAcquisitionProvider get(DataAcquisitionProperties.SourceType type) {
        return Optional.ofNullable(providers.get(type))
                .orElseThrow(() -> new IllegalStateException(
                        "No provider found for type: " + type));
    }
}
