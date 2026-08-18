package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.JWTClaimsSet;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service.CallerIdentityResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;

/**
 * Infrastructure adapter that resolves caller identity from the bearer token attached
 * to the current {@link SecurityContextHolder} authentication.
 * <p>
 * <b>IMPORTANT — not cryptographically verified (EUD-226 T30/T31).</b> This adapter
 * decodes the JWT payload without verifying its signature. It is only as trustworthy
 * as the upstream {@code DataAcquisitionIssuanceAuthenticationProvider}, which is
 * itself an explicit bypass stub (see its own TODOs) that does not validate signatures
 * either. This is the closest honest approximation available today without introducing
 * a full OAuth2 resource-server / {@code JwtDecoder} setup, which does not exist in this
 * repository (see quality-report.md §2.1 B2 remediation, EUD-226 code-review 2026-08-17).
 * A tech-debt item tracks the full signature-verified implementation as a separate,
 * future Story — do not treat the values returned here as tamper-proof.
 * </p>
 * <p>
 * Claim shape mirrors the one already used elsewhere in this codebase (see
 * {@code MockDataAcquisitionAdapter}) and in the sibling {@code eudistack-core-issuer}
 * repo's {@code AccessTokenServiceImpl}: {@code mandator.organizationIdentifier}.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtCallerIdentityResolver implements CallerIdentityResolver {

    private static final String ORGANIZATION_ID_CLAIM_PATH = "mandator.organizationIdentifier";

    private final ObjectMapper objectMapper;

    @Override
    public Optional<String> resolveOrganizationId() {
        return resolveClaimsPayload()
                .map(payload -> resolveJsonPath(payload, ORGANIZATION_ID_CLAIM_PATH))
                .filter(value -> value != null && !value.isBlank());
    }

    @Override
    public Optional<String> resolveActor() {
        return resolveOrganizationId();
    }

    private Optional<JsonNode> resolveClaimsPayload() {
        return currentBearerToken().flatMap(this::parsePayload);
    }

    private Optional<String> currentBearerToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getCredentials() instanceof String token)) {
            log.debug("No authenticated caller with a bearer token credential in the security context");
            return Optional.empty();
        }
        return Optional.of(token);
    }

    private Optional<JsonNode> parsePayload(String token) {
        try {
            JWTClaimsSet claimsSet = JWTParser.parse(token).getJWTClaimsSet();
            return Optional.of(objectMapper.valueToTree(claimsSet.toJSONObject()));
        } catch (ParseException e) {
            log.debug("Failed to parse bearer token as JWT for caller identity resolution: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private String resolveJsonPath(JsonNode root, String dotPath) {
        JsonNode current = root;
        for (String segment : dotPath.split("\\.")) {
            if (current == null || current.isMissingNode() || current.isNull()) {
                return null;
            }
            current = current.get(segment);
        }
        return (current != null && !current.isMissingNode() && !current.isNull())
                ? current.asText()
                : null;
    }
}
