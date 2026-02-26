package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.RemoteSignatureConfigDto;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client.CoreSigningConfigClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IssuanceConfig {

    private final CoreSigningConfigClient coreSigningConfigClient;
    private final SignatureConfig signatureConfig;
    private final RemoteSignatureConfig remoteSignatureConfig;

    @Bean
    public ApplicationRunner pushSigningConfigAtStartup() {
        return args -> {
            String provider = signatureConfig.getProvider();
            log.info("Enterprise starting. Selected signing provider: {}", provider);

            SigningConfigPushRequest request = getSigningConfigPushRequest(provider);

            int maxAttempts = 4;
            long sleepMs = 2000;

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    coreSigningConfigClient.pushSigningConfig(request);
                    log.info("Signing config pushed to Core (attempt {}/{})", attempt, maxAttempts);
                    return;

                } catch (Exception ex) {
                    if (attempt == maxAttempts) {
                        log.warn("Could not push signing config to Core after {} attempts. Core may not be ready.", maxAttempts, ex);
                        return;
                    }

                    log.warn("Could not push signing config to Core (attempt {}/{}). Retrying in {} ms. Cause: {}",
                            attempt, maxAttempts, sleepMs, ex.toString());

                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("Retry interrupted. Skipping push signing config.");
                        return;
                    }
                }
            }
        };
    }

    private SigningConfigPushRequest getSigningConfigPushRequest(String provider) {
        RemoteSignatureConfigDto remote = new RemoteSignatureConfigDto(
                remoteSignatureConfig.getRemoteSignatureType(),
                remoteSignatureConfig.getRemoteSignatureDomain(),
                remoteSignatureConfig.getRemoteSignatureSignPath(),
                remoteSignatureConfig.getRemoteSignatureClientId(),
                remoteSignatureConfig.getRemoteSignatureClientSecret(),
                remoteSignatureConfig.getRemoteSignatureCredentialId(),
                remoteSignatureConfig.getRemoteSignatureCredentialPassword(),
                remoteSignatureConfig.getCertificateInfoCacheTtl() != null
                        ? remoteSignatureConfig.getCertificateInfoCacheTtl().toString()
                        : null
        );

        return new SigningConfigPushRequest(provider, remote);
    }
}