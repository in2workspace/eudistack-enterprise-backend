package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
public class DataAcquisitionAuthenticationFilter extends AuthenticationFilter {

    public DataAcquisitionAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AuthenticationConverter authenticationConverter,
            RequestMatcher requestMatcher
    ) {
        super(authenticationManager, authenticationConverter);

        setRequestMatcher(requestMatcher);

        setSuccessHandler((request, response, _) ->
                log.debug("Authentication succeeded - {} {} - with response status: {}",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus()));

        setFailureHandler((request, response, _) ->
                {
                    log.debug("Authentication failed - {} {}",
                            request.getMethod(),
                            request.getRequestURI());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
        );
    }
}
