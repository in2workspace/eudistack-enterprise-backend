package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.IssuanceException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.PreSubmittedCredentialDataRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.IssuanceHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssuanceHttpClientImpl implements IssuanceHttpClient {

    private static final String ISSUANCES_PATH = "/backoffice/v1/issuances";

    private final RestClient issuerCoreBackendRestClient;

    @Override
    public void executeIssuanceRequest(String bearerToken, PreSubmittedCredentialDataRequest request) {
        try {
            issuerCoreBackendRestClient.post()
                    .uri(ISSUANCES_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .retrieve()
                    .toBodilessEntity();
            log.debug("Credential issuance request sent successfully");
        } catch (RestClientException e) {
            log.error("Failed to call issuer core backend: {}", e.getMessage());
            throw new IssuanceException("Failed to issue credential", e);
        }
    }
}
