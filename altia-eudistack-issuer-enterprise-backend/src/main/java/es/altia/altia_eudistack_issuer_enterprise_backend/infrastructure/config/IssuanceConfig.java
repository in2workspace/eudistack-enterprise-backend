package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.RemoteSignatureConfigDto;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.SigningConfigHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IssuanceConfig {

    private final SigningConfigHttpClient signingConfigHttpClient;
    private final SignatureConfig signatureConfig;
    private final RemoteSignatureConfig remoteSignatureConfig;

    @Bean
    public ApplicationRunner pushSigningConfigAtStartup() {
        return args -> {
            String provider = signatureConfig.getProvider();
            log.info("Enterprise starting. Selected signing provider: {}", provider);

            try {
                SigningConfigPushRequest request = getSigningConfigPushRequest(provider);
                signingConfigHttpClient.executeSigningConfigRequest(request);
                log.info("Signing config pushed to Core");
            } catch (Exception ex) {
                log.error("Failed to push signing config to Core", ex);
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