package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.IssuanceException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.PreSubmittedCredentialDataRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.IssuerCoreBackendProperties;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.OutgoingRequestLoggingInterceptor;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest.RestClientConfig;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.IssuanceHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest
@Import({IssuanceHttpClientImpl.class,
        RestClientConfig.class,
        OutgoingRequestLoggingInterceptor.class})
class IssuanceHttpClientImplTest {

    private static final String BASE_URL = "http://issuer-core-backend";
    private static final String ISSUANCES_PATH = "/backoffice/v1/issuances";
    private static final String BEARER_TOKEN = "Bearer test-token";

    @Autowired
    private IssuanceHttpClient issuanceHttpClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IssuerCoreBackendProperties properties;

    @BeforeEach
    void setup() {
        when(properties.url()).thenReturn(BASE_URL);
    }

    @Test
    void executeIssuanceRequest_ValidRequest_ExecutesSuccessfully() {
        // Arrange
        PreSubmittedCredentialDataRequest request = buildRequest();

        mockServer.expect(requestTo(ISSUANCES_PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "schema": "employee_badge",
                          "format": "vc+sd-jwt",
                          "payload": {
                            "subjectId": "user-123",
                            "name": "User"
                          },
                          "operation_mode": "PRE_SUBMITTED",
                          "response_uri": "https://enterprise.example.com/callback",
                          "issuance_notification_uri": "https://enterprise.example.com/notifications",
                          "email": "user@example.com"
                        }
                        """))
                .andRespond(withStatus(HttpStatus.OK));

        // Act & Assert
        assertThatCode(() -> issuanceHttpClient.executeIssuanceRequest(BEARER_TOKEN, request))
                .doesNotThrowAnyException();

        mockServer.verify();
    }

    @Test
    void executeIssuanceRequest_CoreReturnsServerError_ThrowsIssuanceException() {
        // Arrange
        PreSubmittedCredentialDataRequest request = buildRequest();

        mockServer.expect(requestTo(ISSUANCES_PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        assertThatThrownBy(() -> issuanceHttpClient.executeIssuanceRequest(BEARER_TOKEN, request))
                .isInstanceOf(IssuanceException.class)
                .hasMessage("Failed to issue credential")
                .hasCauseInstanceOf(RestClientException.class);

        mockServer.verify();
    }

    private PreSubmittedCredentialDataRequest buildRequest() {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("subjectId", "user-123");
        payload.put("name", "User");

        return PreSubmittedCredentialDataRequest.builder()
                .schema("employee_badge")
                .format("vc+sd-jwt")
                .payload(payload)
                .operationMode("PRE_SUBMITTED")
                .responseUri("https://enterprise.example.com/callback")
                .issuanceNotificationUri("https://enterprise.example.com/notifications")
                .email("user@example.com")
                .build();
    }
}