package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH_POST_PATTERN;

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
}
