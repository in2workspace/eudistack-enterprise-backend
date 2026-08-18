package es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model;

/**
 * Enumeration of audit event types for security-relevant actions.
 * <p>
 * Each event type represents a distinct business operation that must be audited
 * for compliance, traceability, and security monitoring. Events are emitted by
 * application workflows and persisted/processed by the audit infrastructure.
 * </p>
 * <p>
 * EUD-226: Added ORGANIZATION_CONTACT_UPDATED and ORGANIZATION_CONTACT_AUTO_PREFILLED
 * for tracking organization contact email lifecycle events.
 * </p>
 */
public enum AuditEventType {

    /**
     * Organization contact email updated manually by an operator.
     * <p>
     * Emitted when an authorized user (Caso B or Caso C with write powers)
     * explicitly updates the organization's contact email via the portal.
     * </p>
     * <p>
     * Context: EUD-226, AC-02 (manual update + audit).
     * </p>
     */
    ORGANIZATION_CONTACT_UPDATED,

    /**
     * Organization contact email auto-prefilled during credential issuance.
     * <p>
     * Emitted when the system automatically sets the organization's contact email
     * for the first time, using the email from the issuance session. This happens
     * silently during the first credential issuance for an organization that has
     * no contact email configured.
     * </p>
     * <p>
     * Context: EUD-226, AC-05 (auto-prefill on first issuance).
     * </p>
     */
    ORGANIZATION_CONTACT_AUTO_PREFILLED

    // Additional event types will be added here as new features require audit trails
    // (e.g., CREDENTIAL_ISSUED, CREDENTIAL_REVOKED, POWER_GRANTED, etc.)
}
