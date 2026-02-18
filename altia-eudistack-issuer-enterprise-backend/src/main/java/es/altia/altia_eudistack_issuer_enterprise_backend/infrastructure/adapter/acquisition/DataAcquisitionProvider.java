package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;

public interface DataAcquisitionProvider {

    String acquire(String credentialConfigurationId, String subjectIdentifier);

    boolean supports(String credentialConfigurationId);

    DataAcquisitionProperties.SourceType getSupportedType();
}
