package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class DataAcquisitionAuthenticationConverterTest {

    @Test
    void shouldConvertBearerHeaderToPreAuthenticatedAuthenticationToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Bearer test-token");

        Authentication authentication = new DataAcquisitionAuthenticationConverter().convert(request);

        assertThat(authentication).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isNull();
        assertThat(authentication.getCredentials()).isEqualTo("test-token");
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenAuthorizationHeaderIsMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn(null);

        assertThatThrownBy(() -> new DataAcquisitionAuthenticationConverter().convert(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Bearer token");
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenAuthorizationHeaderDoesNotStartWithBearerPrefix() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Basic abc123");

        assertThatThrownBy(() -> new DataAcquisitionAuthenticationConverter().convert(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Bearer token");
    }

    @Test
    void shouldReturnEmptyTokenWhenBearerHeaderContainsOnlyPrefix() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Bearer ");

        Authentication authentication = new DataAcquisitionAuthenticationConverter().convert(request);

        assertThat(authentication).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isNull();
        assertThat(authentication.getCredentials()).isEqualTo("");
    }
}
