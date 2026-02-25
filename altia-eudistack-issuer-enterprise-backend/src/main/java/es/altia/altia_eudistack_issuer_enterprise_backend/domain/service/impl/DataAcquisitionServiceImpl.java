package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.DataAcquisitionProviderRegistry;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.DataAcquisitionService;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.DataAcquisitionConfiguration;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataAcquisitionServiceImpl implements DataAcquisitionService {

    private final DataAcquisitionConfiguration configuration;
    private final DataAcquisitionProviderRegistry providerRegistry;

    @Override
    public String acquire(String credentialConfigurationId,
                          String subjectIdentifier) {
        log.debug("Acquiring data for credential configuration {} and subject {}",
                credentialConfigurationId, subjectIdentifier);

        DataAcquisitionProperties.Source source =
                configuration.sourcesByCredentialConfigurationId()
                        .get(credentialConfigurationId);

        if (source == null) {
            throw new IllegalArgumentException(
                    "Unknown credentialConfigurationId: " + credentialConfigurationId);
        }

        DataAcquisitionProvider provider =
                providerRegistry.get(source.type());

        return provider.acquire(credentialConfigurationId, subjectIdentifier);
    }
}
