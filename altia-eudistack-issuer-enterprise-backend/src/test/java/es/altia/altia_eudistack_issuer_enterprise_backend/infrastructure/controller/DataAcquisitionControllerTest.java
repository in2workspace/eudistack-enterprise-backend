package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.DataAcquisitionWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.DataAcquisitionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataAcquisitionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("dev")
class DataAcquisitionControllerTest {

    private static final String BEARER_TOKEN = "Bearer test-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DataAcquisitionWorkflow dataAcquisitionWorkflow;

    @Test
    void AcquireData_WithValidRequest_ReturnsNoContent() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new DataAcquisitionRequest("employee_badge", "subject-123", "user@example.com")
        );

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        then(dataAcquisitionWorkflow).should()
                .execute(BEARER_TOKEN, "employee_badge", "subject-123", "user@example.com");
        then(dataAcquisitionWorkflow).shouldHaveNoMoreInteractions();
    }

    @Test
    void AcquireData_WithNullHolderEmail_ReturnsNoContent() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new DataAcquisitionRequest("employee_badge", "subject-123", null)
        );

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        then(dataAcquisitionWorkflow).should()
                .execute(BEARER_TOKEN, "employee_badge", "subject-123", null);
        then(dataAcquisitionWorkflow).shouldHaveNoMoreInteractions();
    }

    @Test
    void AcquireData_WithoutAuthorizationHeader_ReturnsBadRequest() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new DataAcquisitionRequest("employee_badge", "subject-123", "user@example.com")
        );

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    @ParameterizedTest
    @MethodSource("invalidRequestBodies")
    void AcquireData_WithInvalidRequestBody_ReturnsBadRequest(String requestBody) throws Exception {
        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    @Test
    void AcquireData_WithoutRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    private static Stream<String> invalidRequestBodies() {
        return Stream.of(
                """
                {
                  "credentialConfigurationId": "",
                  "subjectIdentifier": "subject-123",
                  "holderEmail": "user@example.com"
                }
                """,
                """
                {
                  "credentialConfigurationId": "employee_badge",
                  "subjectIdentifier": "",
                  "holderEmail": "user@example.com"
                }
                """,
                """
                {
                  "subjectIdentifier": "subject-123",
                  "holderEmail": "user@example.com"
                }
                """,
                """
                {
                  "credentialConfigurationId": "employee_badge",
                  "holderEmail": "user@example.com"
                }
                """
        );
    }
}