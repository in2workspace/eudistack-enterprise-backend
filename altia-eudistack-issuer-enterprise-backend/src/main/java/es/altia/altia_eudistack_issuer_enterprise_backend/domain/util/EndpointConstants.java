package es.altia.altia_eudistack_issuer_enterprise_backend.domain.util;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class EndpointConstants {

    @SuppressWarnings("squid:S1075")
    public static final String AUTHENTIC_SOURCE_ISSUANCE_PATH = "/api/v1/issuances/authentic-source";
    private static final String HEALTH_PATH = "/health";

    public static final PathPatternRequestMatcher AUTHENTIC_SOURCE_ISSUANCE_PATH_POST_PATTERN =
            PathPatternRequestMatcher
                    .withDefaults()
                    .matcher(HttpMethod.POST, AUTHENTIC_SOURCE_ISSUANCE_PATH);

    public static final PathPatternRequestMatcher HEALTH_PATH_GET_PATTERN =
            PathPatternRequestMatcher
                    .withDefaults()
                    .matcher(HttpMethod.GET, HEALTH_PATH);


    private EndpointConstants() {
        /* This utility class should not be instantiated */
    }
}
