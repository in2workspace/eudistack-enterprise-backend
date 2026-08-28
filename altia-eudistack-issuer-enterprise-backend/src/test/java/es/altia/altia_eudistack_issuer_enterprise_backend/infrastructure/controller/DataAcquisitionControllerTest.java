package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.DataAcquisitionWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.DataAcquisitionRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(DataAcquisitionController.class)
class DataAcquisitionControllerTest {

    private static final String BEARER_TOKEN = "Bearer test-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DataAcquisitionWorkflow dataAcquisitionWorkflow;

    @NotNull
    private static MockHttpServletRequestBuilder acquireDataRequest() {
        return post(DATA_ACQUISITION_PATH)
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
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

    @Test
    void AcquireData_WithValidRequest_ReturnsNoContent() throws Exception {
        String requestBody = validRequestBody();

        mockMvc.perform(acquireDataRequest()
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(dataAcquisitionWorkflow)
                .execute(BEARER_TOKEN, "employee_badge", "subject-123", "user@example.com");
        verifyNoMoreInteractions(dataAcquisitionWorkflow);
    }

    @Test
    void AcquireData_WithNullHolderEmail_ReturnsNoContent() throws Exception {
        String requestBody = requestBody(null);

        mockMvc.perform(acquireDataRequest()
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(dataAcquisitionWorkflow)
                .execute(BEARER_TOKEN, "employee_badge", "subject-123", null);
        verifyNoMoreInteractions(dataAcquisitionWorkflow);
    }

    @Test
    void AcquireData_WithoutAuthorizationHeader_ReturnsBadRequest() throws Exception {
        String requestBody = validRequestBody();

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(dataAcquisitionWorkflow);
    }

    @ParameterizedTest
    @MethodSource("invalidRequestBodies")
    void AcquireData_WithInvalidRequestBody_ReturnsBadRequest(String requestBody) throws Exception {
        mockMvc.perform(acquireDataRequest()
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(dataAcquisitionWorkflow);
    }

    @Test
    void AcquireData_WithoutRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(acquireDataRequest())
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(dataAcquisitionWorkflow);
    }

    private String validRequestBody() throws JsonProcessingException {
        return requestBody("user@example.com");
    }

    private String requestBody(String mail) throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                new DataAcquisitionRequest("employee_badge", "subject-123", mail)
        );
    }
}