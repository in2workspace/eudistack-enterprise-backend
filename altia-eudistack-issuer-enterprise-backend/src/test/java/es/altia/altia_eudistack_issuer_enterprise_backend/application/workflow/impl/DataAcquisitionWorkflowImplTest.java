package es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.DataAcquisitionService;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.IssuanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataAcquisitionWorkflowImplTest {

    @Mock
    private DataAcquisitionService dataAcquisitionService;

    @Mock
    private IssuanceService issuanceService;

    @InjectMocks
    private DataAcquisitionWorkflowImpl dataAcquisitionWorkflow;

    private final String bearerToken = "Bearer mocked-token";

    @Test
    void execute_ShouldExecuteSuccessfully() {
        String credentialConfigurationId = "credential-configuration-id";
        String subjectIdentifier = "subject-identifier";

        String acquiredData = "mocked-result";
        when(dataAcquisitionService.acquire(credentialConfigurationId, subjectIdentifier))
                .thenReturn(acquiredData);

        String holderEmail = "example@example.example";
        dataAcquisitionWorkflow.execute(bearerToken, credentialConfigurationId, subjectIdentifier, holderEmail);

        verify(dataAcquisitionService, times(1))
                .acquire(credentialConfigurationId, subjectIdentifier);

        verify(issuanceService, times(1))
                .issueCredential(bearerToken, credentialConfigurationId, acquiredData, holderEmail);

        verifyNoMoreInteractions(dataAcquisitionService, issuanceService);
    }
}




