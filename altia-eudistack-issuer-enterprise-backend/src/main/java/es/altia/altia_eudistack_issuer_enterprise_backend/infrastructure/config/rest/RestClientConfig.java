package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.IssuerCoreBackendProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final OutgoingRequestLoggingInterceptor outgoingRequestLoggingInterceptor;

    @Bean
    public RestClient issuerCoreBackendRestClient(IssuerCoreBackendProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.url())
                .requestInterceptor(outgoingRequestLoggingInterceptor)
                .build();
    }
}

