package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataAcquisitionAuthenticationConverterTest {

    @Test
    void Convert_AuthorizationHeaderContainsBearerToken_ReturnsPreAuthenticatedAuthenticationToken() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");

        // Act
        Authentication authentication = new DataAcquisitionAuthenticationConverter().convert(request);

        // Assert
        assertThat(authentication)
                .isInstanceOf(PreAuthenticatedAuthenticationToken.class)
                .extracting(Authentication::getPrincipal, Authentication::getCredentials)
                .containsExactly(null, "test-token");
    }

    @Test
    void Convert_AuthorizationHeaderIsMissing_ThrowsBadCredentialsException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> new DataAcquisitionAuthenticationConverter().convert(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Bearer token");
    }

    @Test
    void Convert_AuthorizationHeaderDoesNotStartWithBearerPrefix_ThrowsBadCredentialsException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        // Act & Assert
        assertThatThrownBy(() -> new DataAcquisitionAuthenticationConverter().convert(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Bearer token");
    }

    @Test
    void Convert_AuthorizationHeaderContainsOnlyBearerPrefix_ReturnsEmptyToken() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        Authentication authentication = new DataAcquisitionAuthenticationConverter().convert(request);

        // Assert
        assertThat(authentication).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isNull();
        assertThat(authentication.getCredentials()).isEqualTo("");
    }
}