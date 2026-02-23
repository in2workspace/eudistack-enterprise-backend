package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client.CoreSigningConfigClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IssuanceConfig {
    /**
     * Allowed values:
     * - in-memory
     * - csc-sign-doc
     * - csc-sign-hash
     */

    private final CoreSigningConfigClient coreSigningConfigClient;

    @Value("${issuer.signing.provider:in-memory}")
    private String provider;

    @Bean
    public ApplicationRunner pushSigningConfigAtStartup() {
        return args -> {
            log.info("Enterprise starting. Selected signing provider: {}", provider);

            coreSigningConfigClient
                    .pushSigningProvider(provider)
                    .retry(3)
                    .onErrorResume(err -> {
                        log.warn("Could not push signing config to Core. Core may not be ready.");
                        return Mono.empty();
                    })
                    .block();
        };
    }
}

