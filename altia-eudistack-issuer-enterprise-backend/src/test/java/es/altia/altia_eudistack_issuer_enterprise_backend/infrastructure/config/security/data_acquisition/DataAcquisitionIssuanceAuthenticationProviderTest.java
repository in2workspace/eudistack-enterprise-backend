package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataAcquisitionIssuanceAuthenticationProviderTest {

    private final DataAcquisitionIssuanceAuthenticationProvider provider =
            new DataAcquisitionIssuanceAuthenticationProvider();

    @Test
    void Authenticate_TokenIsValid_ReturnsAuthenticatedPreAuthenticatedAuthenticationToken() {
        // Arrange
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, "eyJ.valid-token");

        // Act
        Authentication result = provider.authenticate(authentication);

        // Assert
        assertThat(result).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
        assertThat(result.getPrincipal()).isEqualTo("bypassing-authentication-for-now");
        assertThat(result.getCredentials()).isEqualTo("eyJ.valid-token");
        assertThat(result.getAuthorities()).isEqualTo(List.of());
    }

    @Test
    void Authenticate_TokenIsNull_ThrowsBadCredentialsException() {
        // Arrange
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, null);

        // Act & Assert
        assertThatThrownBy(() -> provider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid or missing Bearer token");
    }

    @Test
    void Authenticate_TokenIsBlank_ThrowsBadCredentialsException() {
        // Arrange
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, "   ");

        // Act & Assert
        assertThatThrownBy(() -> provider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid or missing Bearer token");
    }

    @Test
    void Authenticate_TokenDoesNotStartWithExpectedPrefix_ThrowsBadCredentialsException() {
        // Arrange
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, "invalid-token");

        // Act & Assert
        assertThatThrownBy(() -> provider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid or missing Bearer token");
    }

    @Test
    void Supports_AuthenticationTypeIsPreAuthenticatedAuthenticationToken_ReturnsTrue() {
        // Act
        boolean result = provider.supports(PreAuthenticatedAuthenticationToken.class);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void Supports_AuthenticationTypeIsSubclassOfPreAuthenticatedAuthenticationToken_ReturnsTrue() {
        // Arrange
        class CustomPreAuthenticatedAuthenticationToken extends PreAuthenticatedAuthenticationToken {
            CustomPreAuthenticatedAuthenticationToken() {
                super(null, "eyJ.valid-token");
            }
        }

        // Act
        boolean result = provider.supports(CustomPreAuthenticatedAuthenticationToken.class);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void Supports_AuthenticationTypeIsNotPreAuthenticatedAuthenticationToken_ReturnsFalse() {
        // Act
        boolean result = provider.supports(Authentication.class);

        // Assert
        assertThat(result).isFalse();
    }
}