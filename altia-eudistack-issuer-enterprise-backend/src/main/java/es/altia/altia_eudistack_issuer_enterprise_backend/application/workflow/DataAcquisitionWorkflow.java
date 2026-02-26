package es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow;

public interface DataAcquisitionWorkflow {
    void execute(String bearerToken, String credentialConfigurationId, String subjectIdentifier, String holderEmail);
}