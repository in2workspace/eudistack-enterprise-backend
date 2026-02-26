package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;

public interface SigningConfigHttpClient {
    void executeSigningConfigRequest(SigningConfigPushRequest request);
}
