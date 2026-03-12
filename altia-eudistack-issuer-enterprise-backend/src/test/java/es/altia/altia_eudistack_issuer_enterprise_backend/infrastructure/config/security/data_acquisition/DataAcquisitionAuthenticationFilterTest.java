package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class DataAcquisitionAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticationConverter authenticationConverter;

    @Mock
    private RequestMatcher requestMatcher;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DataAcquisitionAuthenticationFilter filter;

    @Test
    void Constructor_RequestMatcherIsProvided_ConfiguresRequestMatcher() {
        // Assert
        Object configuredRequestMatcher = ReflectionTestUtils.getField(filter, "requestMatcher");
        assertThat(configuredRequestMatcher).isSameAs(requestMatcher);
    }

    @Test
    void Constructor_FilterIsCreated_ConfiguresSuccessHandler() throws ServletException, IOException {
        // Arrange
        AuthenticationSuccessHandler successHandler =
                (AuthenticationSuccessHandler) ReflectionTestUtils.getField(filter, "successHandler");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/procedures/acquire");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(successHandler).isNotNull();

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void Constructor_FilterIsCreated_ConfiguresFailureHandlerThatReturnsUnauthorized() throws ServletException, IOException {
        // Arrange
        AuthenticationFailureHandler failureHandler =
                (AuthenticationFailureHandler) ReflectionTestUtils.getField(filter, "failureHandler");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/procedures/acquire");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(failureHandler).isNotNull();

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