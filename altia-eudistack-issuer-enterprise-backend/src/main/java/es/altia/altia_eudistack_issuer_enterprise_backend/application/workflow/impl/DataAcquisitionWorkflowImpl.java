package es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.DataAcquisitionWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.DataAcquisitionService;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.IssuanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataAcquisitionWorkflowImpl implements DataAcquisitionWorkflow {

    private final IssuanceService issuanceService;
    private final DataAcquisitionService dataAcquisitionService;

    @Override
    public void execute(String bearerToken, String credentialConfigurationId, String subjectIdentifier, String holderEmail) {
        String acquiredData = acquireData(credentialConfigurationId, subjectIdentifier);

        issueCredential(bearerToken, credentialConfigurationId, subjectIdentifier, acquiredData, holderEmail);
    }

    private String acquireData(String credentialConfigurationId, String subjectIdentifier) {
        String acquiredData = dataAcquisitionService.acquire(credentialConfigurationId, subjectIdentifier);

        log.debug("Data acquired for credential configuration {} and subject {}",
                credentialConfigurationId,
                subjectIdentifier);

        return acquiredData;
    }

    private void issueCredential(String bearerToken, String credentialConfigurationId, String subjectIdentifier, String acquiredData, String holderEmail) {
        issuanceService.issueCredential(bearerToken, credentialConfigurationId, acquiredData, holderEmail);

        log.debug("Credential issuance completed successfully [credentialConfigurationId={}, subjectIdentifier={} holderEmail={}]",
                credentialConfigurationId, subjectIdentifier, holderEmail);
    }
}
