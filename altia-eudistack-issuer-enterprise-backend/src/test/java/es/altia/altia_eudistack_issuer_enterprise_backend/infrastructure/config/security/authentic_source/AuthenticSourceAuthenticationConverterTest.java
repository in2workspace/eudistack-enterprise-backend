package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.authentic_source;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticSourceAuthenticationConverterTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthenticSourceAuthenticationConverter authenticSourceAuthenticationConverter;

    @Test
    void authenticSourceIssuance_AuthorizationHeaderWithoutBearerPrefix_ShouldReturn401() {

        when(httpServletRequest.getHeader("Authorization"))
                .thenReturn("Basic whatever");

        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> authenticSourceAuthenticationConverter.convert(httpServletRequest))
                .withMessageContaining("Invalid Bearer");
    }
}