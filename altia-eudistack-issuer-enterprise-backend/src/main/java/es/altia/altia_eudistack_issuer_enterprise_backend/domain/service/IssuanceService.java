package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service;

public interface IssuanceService {
    void issueCredential(String bearerToken, String credentialConfigurationId, String acquiredData, String holderEmail);
}
