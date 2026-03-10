package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.IssuanceException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.RemoteSignatureConfigDto;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.SignatureConfig;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.IssuerCoreBackendProperties;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.OutgoingRequestLoggingInterceptor;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.RestClientConfig;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.SigningConfigHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest
@Import({
        SigningConfigHttpClientImpl.class,
        RestClientConfig.class,
        OutgoingRequestLoggingInterceptor.class
})
class SigningConfigHttpClientImplTest {

    private static final String CORE_DOMAIN = "http://core-backend";
    private static final String CONFIG_PATH = "/internal/signing/config";
    private static final String FULL_URL = CORE_DOMAIN + CONFIG_PATH;

    @Autowired
    private SigningConfigHttpClient signingConfigHttpClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @MockitoBean
    private SignatureConfig signatureConfig;

    @MockitoBean
    private IssuerCoreBackendProperties properties;

    @BeforeEach
    void setUp() {
        when(signatureConfig.getCoreDomain()).thenReturn(CORE_DOMAIN);
        when(properties.url()).thenReturn("http://issuer-core-backend");
    }

    @Test
    void executeSigningConfigRequest_CoreReturnsOk_ExecutesSuccessfully() {
        // Arrange
        SigningConfigPushRequest request = buildRequest();

        mockServer.expect(requestTo(FULL_URL))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "provider": "provider-test",
                          "remoteSignature": {
                            "type": "REMOTE",
                            "url": "https://signature.example.com",
                            "signPath": "/sign",
                            "clientId": "client-id-test",
                            "clientSecret": "client-secret-test",
                            "credentialId": "credential-id-test",
                            "credentialPassword": "credential-password-test",
                            "certificateInfoCacheTtl": "3600"
                          }
                        }
                        """))
                .andRespond(withStatus(HttpStatus.OK));

        // Act & Assert
        assertThatCode(() -> signingConfigHttpClient.executeSigningConfigRequest(request))
                .doesNotThrowAnyException();

        mockServer.verify();
    }

    @Test
    void executeSigningConfigRequest_CoreReturnsServerError_ThrowsIssuanceException() {
        // Arrange
        SigningConfigPushRequest request = buildRequest();

        mockServer.expect(requestTo(FULL_URL))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        assertThatThrownBy(() -> signingConfigHttpClient.executeSigningConfigRequest(request))
                .isInstanceOf(IssuanceException.class)
                .hasMessage("Failed to issue credential")
                .hasCauseInstanceOf(RestClientException.class);

        mockServer.verify();
    }

    private SigningConfigPushRequest buildRequest() {
        RemoteSignatureConfigDto remoteSignature = new RemoteSignatureConfigDto(
                "REMOTE",
                "https://signature.example.com",
                "/sign",
                "client-id-test",
                "client-secret-test",
                "credential-id-test",
                "credential-password-test",
                "3600"
        );

        return new SigningConfigPushRequest("provider-test", remoteSignature);
    }
}