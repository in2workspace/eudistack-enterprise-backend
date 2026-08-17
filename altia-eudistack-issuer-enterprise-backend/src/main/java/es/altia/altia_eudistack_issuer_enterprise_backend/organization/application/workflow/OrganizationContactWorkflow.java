package es.altia.altia_eudistack_issuer_enterprise_backend.organization.application.workflow;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.ContactUpdateSource;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationContactService;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model.AuditEventType;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application layer workflow for organization contact management.
 * <p>
 * Orchestrates read and write operations for organization contact emails,
 * coordinating between the persistence layer (OrganizationContactService port)
 * and the audit layer. This workflow handles both manual updates (operator-driven)
 * and automatic prefill (issuance-driven).
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Query contact information for display in the portal</li>
 *   <li>Persist contact updates (manual or auto-prefill)</li>
 *   <li>Emit audit events for all state changes (ORGANIZATION_CONTACT_UPDATED, ORGANIZATION_CONTACT_AUTO_PREFILLED)</li>
 *   <li>Coordinate transactional boundaries (though actual @Transactional may be added later if needed)</li>
 * </ul>
 * </p>
 * <p>
 * AC coverage: AC-01 (find), AC-02 (update + audit), AC-05 (auto-prefill), AC-06 (tenant isolation via repository).
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationContactWorkflow {

    private final OrganizationContactService organizationContactService;
    private final AuditService auditService;

    /**
     * Finds the contact email for a given organization.
     * <p>
     * This operation is read-only and does NOT emit audit events (reading contact
     * information is not a security-relevant action per the technical design).
     * Tenant isolation is ensured by the repository layer (schema-per-tenant).
     * </p>
     * <p>
     * Covers AC-01: Query contact information.
     * </p>
     *
     * @param orgId the organization identifier, must not be null
     * @return an Optional containing the organization contact if set, or empty if no contact exists
     */
    public Optional<OrganizationContact> findContactByOrganizationId(String orgId) {
        log.info("Finding contact for organization: {}", orgId);

        Optional<OrganizationContact> contact = organizationContactService.findContactByOrganizationId(orgId);

        if (contact.isPresent()) {
            log.info("Contact found for organization: {}", orgId);
        } else {
            log.debug("No contact found for organization: {}", orgId);
        }

        return contact;
    }

    /**
     * Saves or updates the contact email for a given organization.
     * <p>
     * This method orchestrates the persistence operation and emits an audit event
     * indicating the source of the update (manual operator action or automatic prefill).
     * The audit event type varies based on the source:
     * <ul>
     *   <li>MANUAL → ORGANIZATION_CONTACT_UPDATED</li>
     *   <li>AUTO_PREFILL → ORGANIZATION_CONTACT_AUTO_PREFILLED</li>
     * </ul>
     * </p>
     * <p>
     * Tenant isolation is ensured by the repository layer. The workflow does NOT
     * perform authorization checks — those are the responsibility of the controller layer.
     * </p>
     * <p>
     * Covers AC-02: Update contact + audit; AC-05: Auto-prefill during issuance; AC-06: Tenant isolation.
     * </p>
     *
     * @param orgId   the organization identifier, must not be null
     * @param contact the contact to save, must not be null
     * @param source  the source of the update (manual or auto-prefill), must not be null
     */
    public void saveContact(String orgId, OrganizationContact contact, ContactUpdateSource source) {
        log.info("Saving contact for organization: {} (source: {})", orgId, source);

        // Fetch old contact for audit trail (before update)
        Optional<OrganizationContact> oldContact = organizationContactService.findContactByOrganizationId(orgId);

        // Persist the new contact
        organizationContactService.saveContact(orgId, contact, source);

        // Emit audit event based on update source
        emitAuditEvent(orgId, oldContact.orElse(null), contact, source);

        log.info("Contact saved successfully for organization: {} (source: {})", orgId, source);
    }

    /**
     * Conditional auto-prefill of contact email during credential issuance.
     * <p>
     * This method is invoked by the issuance workflow AFTER a successful credential
     * issuance. It only updates the contact if:
     * <ul>
     *   <li>The organization has NO contact email set (contact_email IS NULL)</li>
     *   <li>The provided email is not null (i.e., the session contains an email)</li>
     * </ul>
     * If either condition fails, this method returns without making changes (no-op).
     * </p>
     * <p>
     * Edge case handling:
     * <ul>
     *   <li>EC-02: Existing contact → no-op (does not overwrite)</li>
     *   <li>EC-03: No email in session → no-op (cannot prefill)</li>
     * </ul>
     * </p>
     * <p>
     * Covers AC-05: Auto-prefill on first issuance; EC-02, EC-03.
     * </p>
     *
     * @param orgId the organization identifier, must not be null
     * @param email the email from the issuance session, may be null
     */
    public void autoPrefillContactIfAbsent(String orgId, String email) {
        log.debug("Auto-prefill check for organization: {} with email: {}", orgId, email != null ? "[REDACTED]" : "null");

        // EC-03: No email in session → cannot prefill
        if (email == null || email.isBlank()) {
            log.debug("Auto-prefill skipped for organization: {} - no email in session (EC-03)", orgId);
            return;
        }

        // Check if contact already exists
        Optional<OrganizationContact> existingContact = organizationContactService.findContactByOrganizationId(orgId);

        // EC-02: Contact already exists → do not overwrite
        if (existingContact.isPresent()) {
            log.debug("Auto-prefill skipped for organization: {} - contact already exists (EC-02)", orgId);
            return;
        }

        // All conditions met → prefill
        log.info("Auto-prefilling contact for organization: {} from issuance session", orgId);
        OrganizationContact newContact = new OrganizationContact(email);
        saveContact(orgId, newContact, ContactUpdateSource.AUTO_PREFILL);
    }

    /**
     * Emits an audit event for the contact update.
     * <p>
     * Event type depends on the update source:
     * <ul>
     *   <li>MANUAL → ORGANIZATION_CONTACT_UPDATED</li>
     *   <li>AUTO_PREFILL → ORGANIZATION_CONTACT_AUTO_PREFILLED</li>
     * </ul>
     * </p>
     * <p>
     * The audit service implementation is responsible for capturing contextual metadata
     * (actor, timestamp, tenant, correlation ID) from the current execution context.
     * </p>
     *
     * @param orgId      the organization identifier
     * @param oldContact the previous contact (may be null if none existed)
     * @param newContact the new contact (must not be null)
     * @param source     the source of the update
     */
    private void emitAuditEvent(String orgId, OrganizationContact oldContact, OrganizationContact newContact, ContactUpdateSource source) {
        AuditEventType eventType = switch (source) {
            case MANUAL -> AuditEventType.ORGANIZATION_CONTACT_UPDATED;
            case AUTO_PREFILL -> AuditEventType.ORGANIZATION_CONTACT_AUTO_PREFILLED;
        };

        String oldEmail = oldContact != null ? oldContact.email() : null;
        String newEmail = newContact.email();

        log.debug("Emitting audit event: {} for organization: {}", eventType, orgId);

        auditService.recordOrganizationContactEvent(eventType, orgId, oldEmail, newEmail);
    }
}
