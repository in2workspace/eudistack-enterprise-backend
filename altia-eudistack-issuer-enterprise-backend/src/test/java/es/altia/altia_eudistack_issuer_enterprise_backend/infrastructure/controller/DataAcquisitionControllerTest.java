package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.DataAcquisitionWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.DataAcquisitionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DataAcquisitionControllerTest {

    private static final String BEARER_TOKEN = "Bearer test-token";

    @Mock
    private DataAcquisitionWorkflow dataAcquisitionWorkflow;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        objectMapper = new ObjectMapper();

        DataAcquisitionController controller = new DataAcquisitionController(dataAcquisitionWorkflow);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void shouldAcquireDataSuccessfully() throws Exception {
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
    void shouldAcquireDataSuccessfullyWhenHolderEmailIsNull() throws Exception {
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
    void shouldReturnBadRequestWhenAuthorizationHeaderIsMissing() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new DataAcquisitionRequest("employee_badge", "subject-123", "user@example.com")
        );

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnBadRequestWhenCredentialConfigurationIdIsBlank() throws Exception {
        String requestBody = """
                {
                  "credentialConfigurationId": "",
                  "subjectIdentifier": "subject-123",
                  "holderEmail": "user@example.com"
                }
                """;

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnBadRequestWhenSubjectIdentifierIsBlank() throws Exception {
        String requestBody = """
                {
                  "credentialConfigurationId": "employee_badge",
                  "subjectIdentifier": "",
                  "holderEmail": "user@example.com"
                }
                """;

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnBadRequestWhenCredentialConfigurationIdIsMissing() throws Exception {
        String requestBody = """
                {
                  "subjectIdentifier": "subject-123",
                  "holderEmail": "user@example.com"
                }
                """;

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnBadRequestWhenSubjectIdentifierIsMissing() throws Exception {
        String requestBody = """
                {
                  "credentialConfigurationId": "employee_badge",
                  "holderEmail": "user@example.com"
                }
                """;

        mockMvc.perform(post(DATA_ACQUISITION_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        then(dataAcquisitionWorkflow).shouldHaveNoInteractions();
    }
}