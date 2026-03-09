package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition.DataAcquisitionSecurityConfiguration;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.support.TestSecurityEndpointsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        SecurityConfig.class,
        CorsConfig.class,
        DataAcquisitionSecurityConfiguration.class,
        TestSecurityEndpointsController.class
})
@AutoConfigureMockMvc
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRequireAuthenticationForDataAcquisitionPost() throws Exception {
        mockMvc.perform(post(DATA_ACQUISITION_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldPermitHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPermitOptionsForDataAcquisitionPath() throws Exception {
        mockMvc.perform(options(DATA_ACQUISITION_PATH))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAnyOtherRequest() throws Exception {
        mockMvc.perform(get("/forbidden"))
                .andExpect(status().isForbidden());
    }
}