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
    void shouldAuthenticateWhenTokenIsValid() {
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, "eyJ.valid-token");

        Authentication result = provider.authenticate(authentication);

        assertThat(result).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
        assertThat(result.getPrincipal()).isEqualTo("bypassing-authentication-for-now");
        assertThat(result.getCredentials()).isEqualTo("eyJ.valid-token");
        assertThat(result.getAuthorities()).isEqualTo(List.of());
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenTokenIsNull() {
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, null);

        assertThatThrownBy(() -> provider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid or missing Bearer token");
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenTokenIsBlank() {
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, "   ");

        assertThatThrownBy(() -> provider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid or missing Bearer token");
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenTokenDoesNotStartWithExpectedPrefix() {
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(null, "invalid-token");

        assertThatThrownBy(() -> provider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid or missing Bearer token");
    }

    @Test
    void shouldSupportPreAuthenticatedAuthenticationToken() {
        assertThat(provider.supports(PreAuthenticatedAuthenticationToken.class)).isTrue();
    }

    @Test
    void shouldSupportSubclassesOfPreAuthenticatedAuthenticationToken() {
        class CustomPreAuthenticatedAuthenticationToken extends PreAuthenticatedAuthenticationToken {
            CustomPreAuthenticatedAuthenticationToken() {
                super(null, "eyJ.valid-token");
            }
        }

        assertThat(provider.supports(CustomPreAuthenticatedAuthenticationToken.class)).isTrue();
    }

    @Test
    void shouldNotSupportOtherAuthenticationTypes() {
        assertThat(provider.supports(Authentication.class)).isFalse();
    }
}
