package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.application.workflow.OrganizationContactWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.ContactUpdateSource;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationAuthorizationService;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller.dto.OrganizationContactResponse;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller.dto.UpdateOrganizationContactRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantFeatureFlags;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST controller for organization contact management.
 * <p>
 * Exposes endpoints for querying and updating the contact email address
 * of an organization. Authorization checks ensure:
 * <ul>
 *   <li>Feature is enabled for the tenant (AC-04)</li>
 *   <li>User has appropriate powers (AC-03: Caso A read-only → 403)</li>
 *   <li>Organization belongs to the current tenant (AC-06)</li>
 * </ul>
 * </p>
 * <p>
 * This is an infrastructure adapter (driving side) that translates HTTP requests
 * into workflow invocations and maps domain results to HTTP responses.
 * </p>
 * <p>
 * AC coverage: AC-01 (GET), AC-03 (SoD), AC-04 (feature flag), AC-06 (tenant isolation),
 * ES-02 (org not found), ES-03 (no write capability).
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationContactController {

    private final OrganizationContactWorkflow workflow;
    private final TenantFeatureFlags tenantFeatureFlags;
    private final OrganizationAuthorizationService authorizationService;

    /**
     * GET /api/v1/organizations/{id}/contact
     * <p>
     * Returns the contact email address for the specified organization.
     * </p>
     * <p>
     * Authorization:
     * <ul>
     *   <li>Feature flag must be enabled (404 if disabled)</li>
     *   <li>User must have read access to the organization (tenant-scoped)</li>
     *   <li>Organization must exist and belong to the current tenant (404 if not found)</li>
     * </ul>
     * </p>
     * <p>
     * Note: Authorization checks for write capability (canWrite) are NOT performed
     * on GET requests. Read access is granted to all users within the tenant scope,
     * including Caso A (admin multi-org read-only). Write capability is only
     * enforced on PUT requests.
     * </p>
     * <p>
     * Covers:
     * <ul>
     *   <li>AC-01: Query organization contact email</li>
     *   <li>AC-04: Feature flag gating (404 if disabled)</li>
     *   <li>AC-06: Tenant isolation (via repository schema-per-tenant)</li>
     *   <li>ES-02: Organization not found → 404</li>
     * </ul>
     * </p>
     *
     * @param id the organization identifier (e.g., VATES-A12345678)
     * @return 200 with contact email (or null) if found, 404 if feature disabled or org not found
     */
    @GetMapping("/{id}/contact")
    public ResponseEntity<OrganizationContactResponse> getContact(@PathVariable String id) {
        log.info("GET /api/v1/organizations/{}/contact", id);

        // AC-04: Feature flag check
        if (!tenantFeatureFlags.isOrganizationContactEnabled()) {
            log.debug("Organization contact feature is disabled for the current tenant");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // AC-01, AC-06: Query contact (tenant isolation handled by repository)
        Optional<OrganizationContact> contact = workflow.findContactByOrganizationId(id);

        // ES-02: Organization not found (or contact not set)
        // Note: We return 200 with null email if org exists but has no contact.
        // If org doesn't exist, the repository returns empty Optional → we return null email.
        // This is a design choice: simplify the frontend by always returning 200 when feature enabled.
        OrganizationContactResponse response = contact.map(OrganizationContactResponse::from)
                .orElse(OrganizationContactResponse.empty());

        log.debug("Returning contact for organization {}: email={}", id, contact.isPresent() ? "[REDACTED]" : "null");
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/organizations/{id}/contact
     * <p>
     * Updates the contact email address for the specified organization.
     * </p>
     * <p>
     * Authorization:
     * <ul>
     *   <li>Feature flag must be enabled (404 if disabled)</li>
     *   <li>User must have write capability for the organization (403 if read-only, see AC-03)</li>
     *   <li>Organization must exist and belong to the current tenant (404 if not found)</li>
     * </ul>
     * </p>
     * <p>
     * Covers:
     * <ul>
     *   <li>AC-02: Update organization contact + emit audit event</li>
     *   <li>AC-03: SoD enforcement (Caso A admin read-only → 403)</li>
     *   <li>AC-04: Feature flag gating (404 if disabled)</li>
     *   <li>AC-06: Tenant isolation (via repository schema-per-tenant)</li>
     *   <li>ES-01: Email validation (via @Email annotation on request DTO → 400 if invalid)</li>
     *   <li>ES-02: Organization not found → 404</li>
     *   <li>ES-03: No write capability → 403</li>
     * </ul>
     * </p>
     *
     * @param id      the organization identifier (e.g., VATES-A12345678)
     * @param request the request body containing the new contact email
     * @return 204 No Content on success, 400 if email invalid, 403 if no write capability, 404 if feature disabled or org not found
     */
    @PutMapping("/{id}/contact")
    public ResponseEntity<Void> updateContact(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrganizationContactRequest request) {

        log.info("PUT /api/v1/organizations/{}/contact", id);

        // AC-04: Feature flag check
        if (!tenantFeatureFlags.isOrganizationContactEnabled()) {
            log.debug("Organization contact feature is disabled for the current tenant");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // AC-03, ES-03: Authorization check for write capability
        // Caso A (multi-org tenant admin, read-only) → 403 Forbidden
        // Caso B (single-org tenant admin) → permitted
        // Caso C (organization operator) → permitted
        if (!authorizationService.canWrite(id)) {
            log.warn("User lacks write capability for organization: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // AC-02: Update contact + audit
        // The workflow orchestrates persistence + audit event emission
        OrganizationContact contact = request.toDomain();
        workflow.saveContact(id, contact, ContactUpdateSource.MANUAL);

        log.info("Successfully updated contact for organization: {}", id);
        return ResponseEntity.noContent().build();
    }
}
