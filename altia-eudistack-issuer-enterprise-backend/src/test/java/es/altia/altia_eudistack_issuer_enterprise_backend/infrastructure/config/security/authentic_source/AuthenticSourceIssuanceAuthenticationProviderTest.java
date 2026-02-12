package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.authentic_source;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticSourceIssuanceAuthenticationProviderTest {

    private static final String VALID_TOKEN = "eyj.mocked.jwt.token";

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticSourceIssuanceAuthenticationProvider authenticationProvider;


    @Test
    void authenticate_WithValidToken_ShouldReturnAuthenticatedToken() {
        when(authentication.getCredentials()).thenReturn(VALID_TOKEN);

        Authentication result = authenticationProvider.authenticate(authentication);

        assertThat(result).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
    }

    @Test
    void authenticate_WithValidToken_ShouldReturnTokenWithCorrectCredentials() {
        when(authentication.getCredentials()).thenReturn(VALID_TOKEN);

        Authentication result = authenticationProvider.authenticate(authentication);

        assertThat(result.getCredentials()).isEqualTo(VALID_TOKEN);
    }

    @Test
    void authenticate_WithValidToken_ShouldReturnTokenWithCorrectPrincipal() {
        when(authentication.getCredentials()).thenReturn(VALID_TOKEN);

        Authentication result = authenticationProvider.authenticate(authentication);

        assertThat(result.getPrincipal()).isEqualTo("bypassing-authentication-for-now");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalidToken"})
    void authenticate_WithNullEmptyBlankOrInvalidToken_ShouldThrowBadCredentialsException(String invalidToken) {
        when(authentication.getCredentials()).thenReturn(invalidToken);

        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> authenticationProvider.authenticate(authentication))
                .withMessageContaining("Invalid or missing Bearer token");
    }

    @Test
    void supports_WithPreAuthenticatedAuthenticationToken_ShouldReturnTrue() {
        boolean result = authenticationProvider.supports(PreAuthenticatedAuthenticationToken.class);

        assertThat(result).isTrue();
    }

    @Test
    void supports_WithOtherAuthenticationType_ShouldReturnFalse() {
        boolean result = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);

        assertThat(result).isFalse();
    }
}

