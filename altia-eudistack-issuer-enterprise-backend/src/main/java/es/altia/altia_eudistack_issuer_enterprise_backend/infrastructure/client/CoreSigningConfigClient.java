package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.SignatureConfig;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreSigningConfigClient {

    private final WebClientConfig webClientConfig;
    private final SignatureConfig signatureConfig;

    public Mono<Void> pushSigningConfig(SigningConfigPushRequest request) {
        String coreBaseUrl = signatureConfig.getCoreDomain();
        log.info("Pushing signing provider '{}' to Core at {}", request.provider(), coreBaseUrl);

        return webClientConfig.commonWebClient()
                .put()
                .uri(coreBaseUrl + "/internal/signing/config")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }



}