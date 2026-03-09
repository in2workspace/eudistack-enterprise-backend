package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_adquisition;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition.DataAcquisitionAuthenticationFilter;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DataAcquisitionAuthenticationFilterTest {

    @Test
    void shouldConfigureRequestMatcher() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        AuthenticationConverter authenticationConverter = mock(AuthenticationConverter.class);
        RequestMatcher requestMatcher = mock(RequestMatcher.class);

        DataAcquisitionAuthenticationFilter filter = new DataAcquisitionAuthenticationFilter(
                authenticationManager,
                authenticationConverter,
                requestMatcher
        );

        Object configuredRequestMatcher = ReflectionTestUtils.getField(filter, "requestMatcher");

        assertThat(configuredRequestMatcher).isSameAs(requestMatcher);
    }

    @Test
    void shouldConfigureSuccessHandler() throws ServletException, IOException {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        AuthenticationConverter authenticationConverter = mock(AuthenticationConverter.class);
        RequestMatcher requestMatcher = mock(RequestMatcher.class);

        DataAcquisitionAuthenticationFilter filter = new DataAcquisitionAuthenticationFilter(
                authenticationManager,
                authenticationConverter,
                requestMatcher
        );

        AuthenticationSuccessHandler successHandler =
                (AuthenticationSuccessHandler) ReflectionTestUtils.getField(filter, "successHandler");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/procedures/acquire");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldConfigureFailureHandlerThatReturnsUnauthorized() throws ServletException, IOException {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        AuthenticationConverter authenticationConverter = mock(AuthenticationConverter.class);
        RequestMatcher requestMatcher = mock(RequestMatcher.class);

        DataAcquisitionAuthenticationFilter filter = new DataAcquisitionAuthenticationFilter(
                authenticationManager,
                authenticationConverter,
                requestMatcher
        );

        AuthenticationFailureHandler failureHandler =
                (AuthenticationFailureHandler) ReflectionTestUtils.getField(filter, "failureHandler");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/procedures/acquire");
        MockHttpServletResponse response = new MockHttpServletResponse();

        failureHandler.onAuthenticationFailure(
                request,
                response,
                new org.springframework.security.authentication.BadCredentialsException("Invalid token")
        );

        assertThat(response.getStatus()).isEqualTo(401);
    }
}
