package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.SignatureConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreSigningConfigClient {

    private final WebClient commonWebClient;
    private final SignatureConfig signatureConfig;

    public void pushSigningConfig(SigningConfigPushRequest request) {
        String coreBaseUrl = signatureConfig.getCoreDomain();
        String url = coreBaseUrl + "/internal/signing/config";

        log.info("Pushing signing provider '{}' to Core URL={}", request.provider(), url);

        try {
            commonWebClient
                    .put()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    // fuerza a leer el body de error para loguearlo bien
                    .onStatus(status -> status.isError(), resp ->
                            resp.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(body -> {
                                        log.error("Core returned error. Status={} URL={} Body={}",
                                                resp.statusCode(), url, body);
                                        return Mono.error(new IllegalStateException(
                                                "Core returned " + resp.statusCode() + " for " + url));
                                    })
                    )
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(10))
                    .block();

            log.info("Signing config pushed successfully to Core URL={}", url);

        } catch (WebClientResponseException e) {
            // Por si algo se escapa del onStatus (p.ej. decode)
            log.error("Core returned WebClientResponseException. Status={} URL={} Body={}",
                    e.getStatusCode(), url, e.getResponseBodyAsString(), e);
            throw new IllegalStateException(
                    "Failed to push signing config to Core. Status=" + e.getStatusCode(),
                    e
            );
        } catch (Exception e) {
            log.error("Failed to push signing config to Core. URL={}", url, e);
            throw new IllegalStateException("Failed to push signing config to Core. URL=" + url, e);
        }
    }
}