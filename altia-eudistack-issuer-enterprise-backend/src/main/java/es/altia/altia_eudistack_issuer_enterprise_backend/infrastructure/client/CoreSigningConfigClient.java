package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.SignatureConfig;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreSigningConfigClient {

    private final WebClientConfig webClient;
    private final SignatureConfig signatureConfig;

    public Mono<Void> pushSigningProvider(String provider) {
        String coreBaseUrl = signatureConfig.getCoreDomain();
        log.info("Pushing signing provider '{}' to Core at {}", provider, coreBaseUrl);

        return webClient.commonWebClient()
                .put()
                .uri(coreBaseUrl + "/internal/signing/provider")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("provider", provider))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Successfully pushed signing provider to Core"))
                .doOnError(err -> log.error("Failed to push signing provider to Core: {}", err.getMessage()));
    }
}