package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TestAuditServiceConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestAuditServiceConfiguration.class)
class SecurityConfigTest {

    private static final String VALID_BEARER_TOKEN = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0In0.";
    private static final String ORGANIZATION_CONTACT_PATH = "/api/v1/organizations/org-123/contact";
    private static final String ME_PATH = "/api/v1/me";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantFeatureFlags tenantFeatureFlags;

    @Test
    void SecurityFilterChain_DataAcquisitionPostRequestWithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post(DATA_ACQUISITION_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SecurityFilterChain_OptionsRequestIsSentToDataAcquisitionPath_ReturnsOk() throws Exception {
        mockMvc.perform(options(DATA_ACQUISITION_PATH))
                .andExpect(status().isOk());
    }

    @Test
    void SecurityFilterChain_AnyOtherRequestIsSent_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/forbidden"))
                .andExpect(status().isForbidden());
    }

    @Test
    void SecurityFilterChain_OrganizationContactGetRequestWithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(ORGANIZATION_CONTACT_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SecurityFilterChain_MeGetRequestWithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(ME_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SecurityFilterChain_MeGetRequestWithValidAuthentication_ReachesController() throws Exception {
        when(tenantFeatureFlags.isOrganizationContactEnabled()).thenReturn(false);

        mockMvc.perform(get(ME_PATH)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_BEARER_TOKEN))
                .andExpect(status().isOk());
    }
}