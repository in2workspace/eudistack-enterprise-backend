package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service;

public interface DataAcquisitionService {
    String acquire(String credentialConfigurationId,
                   String subjectIdentifier);
}
