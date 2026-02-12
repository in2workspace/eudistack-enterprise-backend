package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.AuthenticSourceWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.SecurityConfig;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.authentic_source.AuthenticSourceSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@WebMvcTest(AuthenticSourceIssuanceController.class)
@Import({SecurityConfig.class, AuthenticSourceSecurityConfiguration.class})
class AuthenticSourceIssuanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticSourceWorkflow authenticSourceWorkflow;

    @Test
    void issuanceFromAuthenticSource_Success() throws Exception {
        mockMvc.perform(post(EndpointConstants.AUTHENTIC_SOURCE_ISSUANCE_PATH)
                        .header("Authorization", "Bearer eyj.valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authenticSourceWorkflow, times(1)).execute();
    }

    @Test
    void issuanceFromAuthenticSource_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(post(EndpointConstants.AUTHENTIC_SOURCE_ISSUANCE_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authenticSourceWorkflow);
    }

    @Test
    void issuanceFromAuthenticSource_InvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(post(EndpointConstants.AUTHENTIC_SOURCE_ISSUANCE_PATH)
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authenticSourceWorkflow);
    }
}
