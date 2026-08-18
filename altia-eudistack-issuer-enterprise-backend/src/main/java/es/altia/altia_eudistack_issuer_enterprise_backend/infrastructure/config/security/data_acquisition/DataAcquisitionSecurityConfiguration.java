package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH_POST_PATTERN;
import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.ME_PATH_GET_PATTERN;
import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.ORGANIZATION_CONTACT_PATH_GET_PATTERN;
import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.ORGANIZATION_CONTACT_PATH_PUT_PATTERN;

@Configuration
public class DataAcquisitionSecurityConfiguration {

    @Bean
    public DataAcquisitionAuthenticationConverter dataAcquisitionAuthenticationConverter() {
        return new DataAcquisitionAuthenticationConverter();
    }

    @Bean
    public DataAcquisitionIssuanceAuthenticationProvider dataAcquisitionIssuanceAuthenticationProvider() {
        return new DataAcquisitionIssuanceAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager dataAcquisitionIssuanceAuthenticationManager(
            DataAcquisitionIssuanceAuthenticationProvider provider
    ) {
        return new ProviderManager(provider);
    }

    @Bean
    public DataAcquisitionAuthenticationFilter dataAcquisitionIssuanceAuthenticationFilter(
            AuthenticationManager manager,
            DataAcquisitionAuthenticationConverter converter
    ) {
        return new DataAcquisitionAuthenticationFilter(
                manager,
                converter,
                DATA_ACQUISITION_PATH_POST_PATTERN
        );
    }

    /**
     * Authentication filter for the default security filter chain (EUD-226).
     * <p>
     * Reuses the same converter/provider/manager as the data acquisition filter chain —
     * the only authentication mechanism currently available in this codebase — so that
     * {@code /api/v1/organizations/**} and {@code /api/v1/me} are no longer outside every
     * filter chain (see quality-report.md B1/F2). This is the same bearer-token bypass
     * stub used elsewhere in this repo (no cryptographic signature verification yet);
     * see {@link DataAcquisitionIssuanceAuthenticationProvider}.
     * </p>
     */
    @Bean
    public DataAcquisitionAuthenticationFilter defaultAuthenticationFilter(
            AuthenticationManager manager,
            DataAcquisitionAuthenticationConverter converter
    ) {
        RequestMatcher matcher = new OrRequestMatcher(
                ORGANIZATION_CONTACT_PATH_GET_PATTERN,
                ORGANIZATION_CONTACT_PATH_PUT_PATTERN,
                ME_PATH_GET_PATTERN
        );
        return new DataAcquisitionAuthenticationFilter(
                manager,
                converter,
                matcher
        );
    }
}
