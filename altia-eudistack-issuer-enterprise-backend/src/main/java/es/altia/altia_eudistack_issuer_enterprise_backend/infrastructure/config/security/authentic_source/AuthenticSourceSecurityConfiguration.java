package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.authentic_source;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.AUTHENTIC_SOURCE_ISSUANCE_PATH_POST_PATTERN;

@Configuration
public class AuthenticSourceSecurityConfiguration {

    @Bean
    public AuthenticSourceAuthenticationConverter authenticSourceAuthenticationConverter() {
        return new AuthenticSourceAuthenticationConverter();
    }

    @Bean
    public AuthenticSourceIssuanceAuthenticationProvider authenticSourceIssuanceAuthenticationProvider() {
        return new AuthenticSourceIssuanceAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticSourceIssuanceAuthenticationManager(
            AuthenticSourceIssuanceAuthenticationProvider provider
    ) {
        return new ProviderManager(provider);
    }

    @Bean
    public AuthenticSourceIssuanceAuthenticationFilter authenticSourceIssuanceAuthenticationFilter(
            AuthenticationManager manager,
            AuthenticSourceAuthenticationConverter converter
    ) {
        return new AuthenticSourceIssuanceAuthenticationFilter(
                manager,
                converter,
                AUTHENTIC_SOURCE_ISSUANCE_PATH_POST_PATTERN
        );
    }
}
