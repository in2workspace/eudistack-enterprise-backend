package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

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
}