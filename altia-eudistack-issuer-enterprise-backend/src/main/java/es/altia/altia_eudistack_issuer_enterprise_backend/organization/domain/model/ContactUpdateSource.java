package es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model;

/**
 * Source of an organization contact update operation.
 * <p>
 * Distinguishes between manual updates by an operator and automatic
 * pre-filling during credential issuance for audit trail purposes.
 * </p>
 */
public enum ContactUpdateSource {

    /**
     * Contact updated manually by an operator through the management UI.
     */
    MANUAL,

    /**
     * Contact automatically pre-filled during the first credential issuance
     * when no contact was previously set and the user's session provides an email.
     */
    AUTO_PREFILL
}
