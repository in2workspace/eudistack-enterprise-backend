package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link JwtCallerIdentityResolver}.
 * <p>
 * Uses unsigned ({@code alg: none}) JWTs, matching this resolver's documented
 * "not cryptographically verified" limitation (EUD-226 Task 30).
 * </p>
 *
 * @since EUD-226 (Task 30)
 */
@DisplayName("JwtCallerIdentityResolver")
class JwtCallerIdentityResolverTest {

    private final JwtCallerIdentityResolver resolver = new JwtCallerIdentityResolver(new ObjectMapper());

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("resolveOrganizationId returns the mandator.organizationIdentifier claim when present")
    void resolveOrganizationId_withClaim_returnsValue() {
        // Given
        authenticateWithToken(tokenWithOrganizationId("VATES-A15456585"));

        // When
        Optional<String> result = resolver.resolveOrganizationId();

        // Then
        assertThat(result).contains("VATES-A15456585");
    }

    @Test
    @DisplayName("resolveOrganizationId returns empty when there is no authenticated caller")
    void resolveOrganizationId_noAuthentication_returnsEmpty() {
        // Given: SecurityContextHolder cleared (no authentication)

        // When
        Optional<String> result = resolver.resolveOrganizationId();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("resolveOrganizationId returns empty when the token cannot be parsed")
    void resolveOrganizationId_unparseableToken_returnsEmpty() {
        // Given
        authenticateWithToken("not-a-jwt");

        // When
        Optional<String> result = resolver.resolveOrganizationId();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("resolveOrganizationId returns empty when the mandator claim is missing")
    void resolveOrganizationId_missingClaim_returnsEmpty() throws Exception {
        // Given
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject("no-mandator-claim").build();
        authenticateWithToken(new PlainJWT(claims).serialize());

        // When
        Optional<String> result = resolver.resolveOrganizationId();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("resolveActor mirrors resolveOrganizationId")
    void resolveActor_withClaim_returnsValue() {
        // Given
        authenticateWithToken(tokenWithOrganizationId("VATES-A15456585"));

        // When
        Optional<String> result = resolver.resolveActor();

        // Then
        assertThat(result).contains("VATES-A15456585");
    }

    private String tokenWithOrganizationId(String organizationId) {
        try {
            JSONObject mandator = new JSONObject();
            mandator.put("organizationIdentifier", organizationId);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject("test")
                    .claim("mandator", mandator)
                    .build();

            return new PlainJWT(claims).serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build test token", e);
        }
    }

    private void authenticateWithToken(String token) {
        var authentication = new PreAuthenticatedAuthenticationToken("caller", token, List.of());
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
