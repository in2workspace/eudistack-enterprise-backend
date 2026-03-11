package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.DataAcquisitionProviderNotConfiguredException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.TestController.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void HandleDataAcquisitionProviderNotConfiguredException_WhenControllerThrowsException_ReturnsBadRequest()
            throws Exception {
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