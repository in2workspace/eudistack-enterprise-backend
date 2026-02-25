package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.SignatureConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreSigningConfigClient {

    private final WebClient commonWebClient;
    private final SignatureConfig signatureConfig;

    public void pushSigningConfig(SigningConfigPushRequest request) {
        String coreBaseUrl = signatureConfig.getCoreDomain();
        String url = coreBaseUrl + "/internal/signing/config";

        log.info("Pushing signing provider '{}' to Core at {}", request.provider(), coreBaseUrl);

        try {
            commonWebClient
                    .put()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Signing config pushed successfully to Core at {}", url);

        } catch (WebClientResponseException e) {
            log.error(
                    "Core returned error. Status={} URL={} Body={}",
                    e.getStatusCode(), url, e.getResponseBodyAsString(), e
            );
            throw new IllegalStateException(
                    "Failed to push signing config to Core. Status=" + e.getStatusCode(),
                    e
            );
        } catch (Exception e) {
            log.error("Failed to push signing config to Core. URL={}", url, e);
            throw new IllegalStateException("Failed to push signing config to Core.", e);
        }
    }
}