package es.altia.altia_eudistack_issuer_enterprise_backend.domain.util;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class EndpointConstants {

    @SuppressWarnings("squid:S1075")
    public static final String DATA_ACQUISITION_PATH = "/procedures/acquire";
    private static final String HEALTH_PATH = "/health";

    public static final PathPatternRequestMatcher DATA_ACQUISITION_PATH_POST_PATTERN =
            PathPatternRequestMatcher
                    .withDefaults()
                    .matcher(HttpMethod.POST, DATA_ACQUISITION_PATH);

    public static final PathPatternRequestMatcher DATA_ACQUISITION_PATH_OPTIONS_PATTERN =
            PathPatternRequestMatcher
                    .withDefaults()
                    .matcher(HttpMethod.OPTIONS, DATA_ACQUISITION_PATH);

    public static final PathPatternRequestMatcher HEALTH_PATH_GET_PATTERN =
            PathPatternRequestMatcher
                    .withDefaults()
                    .matcher(HttpMethod.GET, HEALTH_PATH);


    private EndpointConstants() {
        /* This utility class should not be instantiated */
    }
}
