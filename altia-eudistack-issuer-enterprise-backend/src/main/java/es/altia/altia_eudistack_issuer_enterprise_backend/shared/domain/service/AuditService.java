package es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model.AuditEventType;

/**
 * Domain port for audit event recording.
 * <p>
 * Defines the contract for emitting security-relevant audit events. Implementations
 * are responsible for:
 * <ul>
 *   <li>Persisting audit events to a durable store (DB, event bus, etc.)</li>
 *   <li>Enriching events with contextual metadata (timestamp, actor, tenant, correlation ID)</li>
 *   <li>Ensuring events are signed or tamper-evident where required by regulation (ENS/NIS2)</li>
 * </ul>
 * </p>
 * <p>
 * This is a minimal port for EUD-226. Future Stories may extend this interface with
 * additional methods (e.g., batch recording, event querying) or define a richer
 * AuditEvent value object with structured fields.
 * </p>
 */
public interface AuditService {

    /**
     * Records an audit event for an organization contact update.
     * <p>
     * This method is invoked by application workflows (e.g., OrganizationContactWorkflow)
     * after a successful state change. The audit service implementation is responsible
     * for capturing contextual metadata (e.g., actor, timestamp, tenant) from the current
     * execution context (SecurityContext / Reactor Context).
     * </p>
     * <p>
     * For EUD-226, this method supports two event types:
     * <ul>
     *   <li>ORGANIZATION_CONTACT_UPDATED — manual update by operator</li>
     *   <li>ORGANIZATION_CONTACT_AUTO_PREFILLED — automatic prefill during issuance</li>
     * </ul>
     * </p>
     *
     * @param eventType      the type of audit event, must not be null
     * @param organizationId the organization identifier, must not be null
     * @param oldValue       the previous contact email (may be null if none existed)
     * @param newValue       the new contact email, must not be null
     */
    void recordOrganizationContactEvent(
            AuditEventType eventType,
            String organizationId,
            String oldValue,
            String newValue
    );

    // Future methods (placeholder for extensibility):
    // void recordCredentialIssuanceEvent(...);
    // void recordPowerChangeEvent(...);
    // List<AuditEvent> queryEvents(AuditQuery query);
}
