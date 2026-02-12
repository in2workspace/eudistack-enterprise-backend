package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.authentic_source;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;

@Slf4j
public class AuthenticSourceIssuanceAuthenticationProvider
        implements AuthenticationProvider {

    // TODO Replace by real JWT validation and claims extraction
    private static final String TEMP_VALID_PREFIX = "eyj";

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        log.debug("Authenticating request with token-based authentication");

        String token = getToken(authentication);

        // TODO Replace by real JWT validation and claims extraction
        validateToken(token);

        return buildPreAuthenticatedAuthenticationToken(token);
    }

    private String getToken(Authentication authentication) {
        return (String) authentication.getCredentials();
    }

    private void validateToken(String token) {
        // TODO Replace by real JWT validation and claims extraction
        if (token == null || token.isBlank() || !token.startsWith(TEMP_VALID_PREFIX)) {
            log.debug("Token is invalid because it is null, blank or does not start with prefix: {}", TEMP_VALID_PREFIX);
            throw new BadCredentialsException("Invalid or missing Bearer token");
        }
    }

    @Nonnull
    private PreAuthenticatedAuthenticationToken buildPreAuthenticatedAuthenticationToken(String token) {
        return new PreAuthenticatedAuthenticationToken(
                "bypassing-authentication-for-now",
                token,
                List.of()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
