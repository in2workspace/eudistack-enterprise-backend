package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.IssuanceException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.SignatureConfig;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.SigningConfigHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigningConfigHttpClientImpl implements SigningConfigHttpClient {

    private static final String CONFIG_PATH = "/internal/signing/config";

    private final SignatureConfig signatureConfig;
    private final RestClient issuerCoreBackendRestClient;

    @Override
    public void executeSigningConfigRequest(SigningConfigPushRequest request) {
        String coreBaseUrl = signatureConfig.getCoreDomain();
        String url = coreBaseUrl + CONFIG_PATH;

        try {
            issuerCoreBackendRestClient.put()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Signing config pushed successfully to Core URL={}", url);

        } catch (RestClientException e) {
            log.error("Failed to push signing config to Core. URL={}", url, e);
            throw new IssuanceException("Failed to issue credential", e);
        }
    }
}
