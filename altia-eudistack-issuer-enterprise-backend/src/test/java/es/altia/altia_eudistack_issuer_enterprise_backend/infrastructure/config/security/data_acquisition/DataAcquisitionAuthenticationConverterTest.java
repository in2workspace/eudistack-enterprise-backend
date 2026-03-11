package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataAcquisitionAuthenticationConverterTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void Convert_AuthorizationHeaderContainsBearerToken_ReturnsPreAuthenticatedAuthenticationToken() {
        // Arrange
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
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> new DataAcquisitionAuthenticationConverter().convert(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Bearer token");
    }

    @Test
    void Convert_AuthorizationHeaderDoesNotStartWithBearerPrefix_ThrowsBadCredentialsException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        // Act & Assert
        assertThatThrownBy(() -> new DataAcquisitionAuthenticationConverter().convert(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Bearer token");
    }

    @Test
    void Convert_AuthorizationHeaderContainsOnlyBearerPrefix_ReturnsEmptyToken() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        Authentication authentication = new DataAcquisitionAuthenticationConverter().convert(request);

        // Assert
        assertThat(authentication).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isNull();
        assertThat(authentication.getCredentials()).isEqualTo("");
    }
}