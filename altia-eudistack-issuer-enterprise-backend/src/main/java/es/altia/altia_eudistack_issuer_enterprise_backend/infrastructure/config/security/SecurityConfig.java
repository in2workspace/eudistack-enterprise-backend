package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security.authentic_source.AuthenticSourceIssuanceAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.AUTHENTIC_SOURCE_ISSUANCE_PATH_POST_PATTERN;
import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.HEALTH_PATH_GET_PATTERN;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authenticSourceIssuanceSecurityFilterChain(
            HttpSecurity http,
            AuthenticSourceIssuanceAuthenticationFilter authenticationFilter) throws Exception {
        return baseStatelessConfig(http)
                .securityMatcher(AUTHENTIC_SOURCE_ISSUANCE_PATH_POST_PATTERN)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTHENTIC_SOURCE_ISSUANCE_PATH_POST_PATTERN).authenticated()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(
                        new SecurityChainLoggingFilter("authenticSourceIssuanceSecurityFilterChain [1]"),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return baseStatelessConfig(http)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HEALTH_PATH_GET_PATTERN).permitAll()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(
                        new SecurityChainLoggingFilter("defaultSecurityFilterChain [2]"),
                        UsernamePasswordAuthenticationFilter.class
                )
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
