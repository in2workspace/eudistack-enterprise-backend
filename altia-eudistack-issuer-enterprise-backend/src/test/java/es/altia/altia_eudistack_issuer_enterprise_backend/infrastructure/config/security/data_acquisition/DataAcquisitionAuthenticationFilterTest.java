package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

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
    void Constructor_RequestMatcherIsProvided_ConfiguresRequestMatcher() {
        // Arrange
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        AuthenticationConverter authenticationConverter = mock(AuthenticationConverter.class);
        RequestMatcher requestMatcher = mock(RequestMatcher.class);

        // Act
        DataAcquisitionAuthenticationFilter filter = new DataAcquisitionAuthenticationFilter(
                authenticationManager,
                authenticationConverter,
                requestMatcher
        );

        // Assert
        Object configuredRequestMatcher = ReflectionTestUtils.getField(filter, "requestMatcher");
        assertThat(configuredRequestMatcher).isSameAs(requestMatcher);
    }

    @Test
    void Constructor_FilterIsCreated_ConfiguresSuccessHandler() throws ServletException, IOException {
        // Arrange
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

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void Constructor_FilterIsCreated_ConfiguresFailureHandlerThatReturnsUnauthorized() throws ServletException, IOException {
        // Arrange
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

        // Act
        failureHandler.onAuthenticationFailure(
                request,
                response,
                new org.springframework.security.authentication.BadCredentialsException("Invalid token")
        );

        // Assert
        assertThat(response.getStatus()).isEqualTo(401);
    }
}