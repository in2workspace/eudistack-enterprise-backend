package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.IssuanceException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.PreSubmittedCredentialDataRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.IssuanceHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssuanceServiceImplTest {

    private static final String CREDENTIAL_CONFIGURATION_ID = "test-credential-config";
    private static final String HOLDER_EMAIL = "holder@example.com";
    private static final String PAYLOAD_EMAIL = "payload@example.com";
    private static final String ACQUIRED_DATA = "{\"name\":\"John Doe\"}";
    private static final String ACQUIRED_DATA_WITH_EMAIL = "{\"name\":\"John Doe\",\"email\":\"payload@example.com\"}";
    private static final String ACQUIRED_DATA_WITH_BLANK_EMAIL = "{\"name\":\"John Doe\",\"email\":\"\"}";
    private static final String INVALID_JSON = "not-valid-json";

    @Mock
    private IssuanceHttpClient issuanceHttpClient;

    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private IssuanceServiceImpl issuanceService;

    private final String bearerToken = "Bearer mocked-token";

    @Test
    void issueCredential_ShouldBuildRequestWithCorrectSchema() {
        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().schema()).isEqualTo(CREDENTIAL_CONFIGURATION_ID);
    }

    @Test
    void issueCredential_ShouldBuildRequestWithDefaultFormat() {
        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().format()).isEqualTo("jwt_vc_json");
    }

    @Test
    void issueCredential_ShouldBuildRequestWithSyncOperationMode() {
        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().operationMode()).isEqualTo("SYNC");
    }

    @Test
    void issueCredential_ShouldBuildRequestWithParsedPayload() throws JsonProcessingException {
        JsonNode payloadNode = objectMapper.readTree(ACQUIRED_DATA);

        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().payload()).isEqualTo(payloadNode);
    }

    @Test
    void issueCredential_ShouldUseHolderEmailWhenPayloadHasNoEmail() {
        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().email()).isEqualTo(HOLDER_EMAIL);
    }

    @Test
    void issueCredential_ShouldUsePayloadEmailWhenPresent() {
        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA_WITH_EMAIL, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().email()).isEqualTo(PAYLOAD_EMAIL);
    }

    @Test
    void issueCredential_ShouldUseHolderEmailWhenPayloadEmailIsBlank() {
        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA_WITH_BLANK_EMAIL, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().email()).isEqualTo(HOLDER_EMAIL);
    }

    @Test
    void issueCredential_ShouldCallExecuteIssuanceRequest() {
        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA, HOLDER_EMAIL);

        verify(issuanceHttpClient, times(1)).executeIssuanceRequest(bearerToken, any(PreSubmittedCredentialDataRequest.class));
    }

    @Test
    void issueCredential_ShouldThrowIssuanceExceptionWhenJsonParsingFails() {
        assertThatThrownBy(() -> issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, INVALID_JSON, HOLDER_EMAIL))
                .isInstanceOf(IssuanceException.class)
                .hasMessage("Invalid acquired data format");
    }

    @Test
    void issueCredential_ShouldNotCallHttpClientWhenJsonParsingFails() {
        assertThatThrownBy(() ->
                issuanceService.issueCredential(
                        bearerToken, CREDENTIAL_CONFIGURATION_ID,
                        INVALID_JSON,
                        HOLDER_EMAIL
                )
        )
                .isInstanceOf(IssuanceException.class);

        verifyNoInteractions(issuanceHttpClient);
    }

    @Test
    void issueCredential_ShouldUseHolderEmailWhenPayloadIsNull() throws JsonProcessingException {
        when(objectMapper.readTree(ACQUIRED_DATA)).thenReturn(null);

        issuanceService.issueCredential(bearerToken, CREDENTIAL_CONFIGURATION_ID, ACQUIRED_DATA, HOLDER_EMAIL);

        ArgumentCaptor<PreSubmittedCredentialDataRequest> captor = ArgumentCaptor.forClass(PreSubmittedCredentialDataRequest.class);
        verify(issuanceHttpClient).executeIssuanceRequest(bearerToken, captor.capture());
        assertThat(captor.getValue().email()).isEqualTo(HOLDER_EMAIL);
    }
}