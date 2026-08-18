package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.data_acquisition.DataAcquisitionAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.*;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain dataAcquisitionSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("dataAcquisitionIssuanceAuthenticationFilter") DataAcquisitionAuthenticationFilter authenticationFilter) throws Exception {
        return baseStatelessConfig(http)
                .securityMatcher(DATA_ACQUISITION_PATH_POST_PATTERN)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(DATA_ACQUISITION_PATH_POST_PATTERN).authenticated()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(
                        new SecurityChainLoggingFilter("dataAcquisitionSecurityFilterChain [1]"),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("defaultAuthenticationFilter") DataAcquisitionAuthenticationFilter authenticationFilter) throws Exception {
        return baseStatelessConfig(http)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HEALTH_PATH_GET_PATTERN).permitAll()
                        .requestMatchers(DATA_ACQUISITION_PATH_OPTIONS_PATTERN).permitAll()
                        .requestMatchers(ORGANIZATION_CONTACT_PATH_GET_PATTERN).authenticated()
                        .requestMatchers(ORGANIZATION_CONTACT_PATH_PUT_PATTERN).authenticated()
                        .requestMatchers(ME_PATH_GET_PATTERN).authenticated()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(
                        new SecurityChainLoggingFilter("defaultSecurityFilterChain [2]"),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults())
                .build();
    }

    private HttpSecurity baseStatelessConfig(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
    }
}
