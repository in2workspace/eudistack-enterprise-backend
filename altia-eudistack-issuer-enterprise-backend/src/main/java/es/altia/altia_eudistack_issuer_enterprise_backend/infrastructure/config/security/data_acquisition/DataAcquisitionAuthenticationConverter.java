package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@Slf4j
public class DataAcquisitionAuthenticationConverter
        implements AuthenticationConverter {

    private static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.debug("Converting request to Authentication token");

        String header = getAuthorizationHeader(request);

        validateBearer(header);

        String token = getToken(header);

        return buildPreAuthenticatedAuthenticationToken(token);
    }

    private String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    private void validateBearer(String header) {
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            log.debug("Invalid Bearer token");
            throw new BadCredentialsException("Invalid Bearer token");
        }
    }

    private String getToken(String header) {
        return header.substring(BEARER_PREFIX.length());
    }

    private static PreAuthenticatedAuthenticationToken buildPreAuthenticatedAuthenticationToken(String token) {
        return new PreAuthenticatedAuthenticationToken(null, token);
    }
}
