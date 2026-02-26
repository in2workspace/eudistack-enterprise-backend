package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.PreSubmittedCredentialDataRequest;

public interface IssuanceHttpClient {
    void executeIssuanceRequest(String bearerToken, PreSubmittedCredentialDataRequest request);
}
