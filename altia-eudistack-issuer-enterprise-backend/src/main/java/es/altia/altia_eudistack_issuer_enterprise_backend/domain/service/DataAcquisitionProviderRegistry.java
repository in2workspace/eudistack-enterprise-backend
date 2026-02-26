package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;

public interface DataAcquisitionProviderRegistry {
    DataAcquisitionProvider get(DataAcquisitionProperties.SourceType type);
}
