package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.IssuerCoreBackendProperties;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.OutgoingRequestLoggingInterceptor;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.RestClientConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class RestClientConfigTest {

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void IssuerCoreBackendRestClient_WhenRequestIsPerformed_SendsExpectedRequest() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"ok\"}")
                .addHeader("Content-Type", "application/json"));

        OutgoingRequestLoggingInterceptor interceptor = new OutgoingRequestLoggingInterceptor();
        RestClientConfig restClientConfig = new RestClientConfig(interceptor);
        IssuerCoreBackendProperties properties =
                new IssuerCoreBackendProperties(mockWebServer.url("/").toString());

        RestClient restClient = restClientConfig.issuerCoreBackendRestClient(properties);

        // Act
        String responseBody = restClient.get()
                .uri("/test-endpoint")
                .retrieve()
                .body(String.class);

        // Assert
        assertThat(responseBody).isEqualTo("{\"status\":\"ok\"}");

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/test-endpoint");
    }
}