package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.PathsDto;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.RemoteSignatureConfigDto;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client.CoreSigningConfigClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final SignatureConfig signatureConfig;
    private final RemoteSignatureConfig remoteSignatureConfig;


    @Bean
    public ApplicationRunner pushSigningConfigAtStartup() {
        return args -> {
            String provider = signatureConfig.getProvider();
            log.info("Enterprise starting. Selected signing provider: {}", provider);

            RemoteSignatureConfigDto remote = new RemoteSignatureConfigDto(
                    remoteSignatureConfig.getRemoteSignatureType(),
                    remoteSignatureConfig.getRemoteSignatureDomain(),
                    new PathsDto(remoteSignatureConfig.getRemoteSignatureSignPath()),
                    remoteSignatureConfig.getRemoteSignatureClientId(),
                    remoteSignatureConfig.getRemoteSignatureClientSecret(),
                    remoteSignatureConfig.getRemoteSignatureCredentialId(),
                    remoteSignatureConfig.getRemoteSignatureCredentialPassword(),
                    remoteSignatureConfig.getCertificateInfoCacheTtl() != null
                            ? remoteSignatureConfig.getCertificateInfoCacheTtl().toString()
                            : null
            );
            SigningConfigPushRequest request = new SigningConfigPushRequest(provider, remote);

            coreSigningConfigClient
                    .pushSigningConfig(request)
                    .retry(3)
                    .onErrorResume(err -> {
                        log.warn("Could not push signing config to Core. Core may not be ready.");
                        return Mono.empty();
                    })
                    .block();
        };
    }
}

