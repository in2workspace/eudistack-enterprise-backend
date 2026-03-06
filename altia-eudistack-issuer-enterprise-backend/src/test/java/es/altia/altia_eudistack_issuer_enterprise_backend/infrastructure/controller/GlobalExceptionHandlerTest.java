package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.DataAcquisitionProviderNotConfiguredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldHandleDataAcquisitionProviderNotConfiguredException() throws Exception {
        mockMvc.perform(get("/test/provider-not-configured")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("DataAcquisitionProviderNotConfiguredException"))
                .andExpect(jsonPath("$.message").value("Data acquisition provider is not configured"))
                .andExpect(jsonPath("$.path").value("/test/provider-not-configured"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/provider-not-configured")
        void throwException() {
            throw new DataAcquisitionProviderNotConfiguredException(
                    "Data acquisition provider is not configured"
            );
        }
    }
}