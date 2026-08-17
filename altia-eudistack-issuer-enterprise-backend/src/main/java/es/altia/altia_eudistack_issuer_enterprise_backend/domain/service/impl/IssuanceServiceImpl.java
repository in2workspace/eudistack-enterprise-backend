package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.IssuanceException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.PreSubmittedCredentialDataRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.IssuanceService;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.IssuanceHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuanceServiceImpl implements IssuanceService {

    private static final String DEFAULT_FORMAT = "jwt_vc_json";
    private static final String OPERATION_MODE = "S";

    private final IssuanceHttpClient issuanceHttpClient;
    private final ObjectMapper objectMapper;

    // Task 12: Dependencies for auto-prefill (EUD-226)
    private final es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationContactService contactService;
    private final es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantFeatureFlags featureFlags;

    @Override
    public void issueCredential(String bearerToken, String credentialConfigurationId, String acquiredData, String holderEmail) {
        PreSubmittedCredentialDataRequest request = buildRequest(credentialConfigurationId, acquiredData, holderEmail);

        executeIssuanceRequest(bearerToken, request);

        // Task 13: Auto-prefill organization contact (EUD-226, AC-05)
        autoPrefillContactBestEffort(bearerToken, holderEmail);
    }

    private PreSubmittedCredentialDataRequest buildRequest(
            String credentialConfigurationId,
            String acquiredData,
            String holderEmail) {
        JsonNode payload = parsePayload(acquiredData);

        return PreSubmittedCredentialDataRequest.builder()
                .schema(credentialConfigurationId)
                .format(DEFAULT_FORMAT)
                .payload(payload)
                .operationMode(OPERATION_MODE)
                .email(getPayloadEmailOrDefault(payload, holderEmail))
                .build();
    }

    private String getPayloadEmailOrDefault(JsonNode payload, String holderEmail) {
        if (payload == null) {
            return holderEmail;
        }

        String payloadEmail = payload.path("email").asText(null);
        return payloadEmail == null || payloadEmail.isBlank()
                ? holderEmail
                : payloadEmail;
    }

    private JsonNode parsePayload(String acquiredData) {
        try {
            return objectMapper.readTree(acquiredData);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse acquired data as JSON: {}", acquiredData);
            throw new IssuanceException("Invalid acquired data format", e);
        }
    }

    private void executeIssuanceRequest(String bearerToken, PreSubmittedCredentialDataRequest request) {
        issuanceHttpClient.executeIssuanceRequest(bearerToken, request);
    }

    /**
     * Auto-prefills organization contact email after successful issuance (AC-05).
     * <p>
     * Fire-and-forget: failures are logged but never propagate to the issuance flow.
     * Only applies to issuances via enterprise data acquisition flow (not direct MFE calls).
     * </p>
     *
     * @param bearerToken the bearer token (contains session claims)
     * @param holderEmail the holder email from the issuance request
     */
    private void autoPrefillContactBestEffort(String bearerToken, String holderEmail) {
        try {
            // AC-05 guard: feature flag disabled → skip
            if (!featureFlags.isOrganizationContactEnabled()) {
                log.debug("Organization contact feature disabled; skipping auto-prefill");
                return;
            }

            // EC-03: session without email → no prefill
            if (holderEmail == null || holderEmail.isBlank()) {
                log.debug("No email in issuance request; skipping contact prefill (EC-03)");
                return;
            }

            // TODO (Task 14): Extract organization ID from bearerToken JWT claims
            // For now, return early until Task 14 implements extractOrganizationId()
            String orgId = extractOrganizationId(bearerToken);
            if (orgId == null || orgId.isBlank()) {
                log.debug("No organization ID in token; skipping contact prefill (Task 14 pending)");
                return;
            }

            // Check if contact already exists
            java.util.Optional<es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact> existingContact =
                    contactService.findContactByOrganizationId(orgId);

            // EC-02: contact exists and has email → do not overwrite
            if (existingContact.isPresent() && existingContact.get().email() != null && !existingContact.get().email().isBlank()) {
                log.debug("Organization {} already has contact email; skipping prefill (EC-02)", orgId);
                return;
            }

            // AC-05: Save contact with AUTO_PREFILL source
            log.info("Auto-prefilling contact email for organization {}", orgId);
            contactService.saveContact(
                    orgId,
                    new es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact(holderEmail),
                    es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.ContactUpdateSource.AUTO_PREFILL
            );

        } catch (Exception e) {
            // Best-effort: log error but don't fail issuance
            log.warn("Failed to auto-prefill contact (non-fatal): {}", e.toString());
        }
    }

    /**
     * Extracts organization ID from the bearer token JWT claims.
     * <p>
     * TODO (Task 14): Parse JWT and extract mandatee.organization_id claim.
     * </p>
     *
     * @param bearerToken the bearer token
     * @return the organization ID, or null if not available
     */
    private String extractOrganizationId(String bearerToken) {
        // Placeholder - Task 14 will implement JWT parsing
        log.debug("TODO (Task 14): Extract organization ID from bearer token");
        return null;
    }
}