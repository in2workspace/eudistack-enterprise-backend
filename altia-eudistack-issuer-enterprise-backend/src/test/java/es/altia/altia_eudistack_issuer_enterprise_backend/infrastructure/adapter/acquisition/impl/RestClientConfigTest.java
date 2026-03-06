package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.IssuerCoreBackendProperties;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.OutgoingRequestLoggingInterceptor;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.RestClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class RestClientConfigTest {

    @Test
    void shouldCreateIssuerCoreBackendRestClient() {
        OutgoingRequestLoggingInterceptor interceptor = new OutgoingRequestLoggingInterceptor();
        RestClientConfig restClientConfig = new RestClientConfig(interceptor);
        IssuerCoreBackendProperties properties =
                new IssuerCoreBackendProperties("https://issuer-core.example.com");

        RestClient restClient = restClientConfig.issuerCoreBackendRestClient(properties);

        assertThat(restClient).isNotNull();
    }
}
