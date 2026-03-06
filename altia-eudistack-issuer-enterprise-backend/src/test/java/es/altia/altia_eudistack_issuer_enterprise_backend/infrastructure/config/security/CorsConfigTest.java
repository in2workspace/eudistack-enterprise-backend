package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void shouldCreateCorsConfigurationSourceWithExpectedValues() {
        CorsConfig corsConfig = new CorsConfig();

        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/any/path");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins()).containsExactly("https://proto.eudistack.net");
        assertThat(configuration.getAllowedMethods()).containsExactly("POST", "OPTIONS");
        assertThat(configuration.getAllowedHeaders()).containsExactly("*");
        assertThat(configuration.getAllowCredentials()).isTrue();
    }
}