package es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.ContactUpdateSource;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;

import java.util.Optional;

/**
 * Domain port for managing organization contact information.
 * <p>
 * Provides operations to retrieve and persist the contact email address
 * of an organization, used for lifecycle notifications. Implementations
 * are responsible for persistence and audit event emission.
 * </p>
 */
public interface OrganizationContactService {

    /**
     * Finds the contact email for the given organization.
     *
     * @param orgId the organization identifier, must not be null
     * @return an Optional containing the organization contact if set, or empty if no contact exists
     */
    Optional<OrganizationContact> findContactByOrganizationId(String orgId);

    /**
     * Saves or updates the contact email for the given organization.
     * <p>
     * Implementations must:
     * <ul>
     *   <li>Persist the contact email to the organization record</li>
     *   <li>Emit an audit event with the appropriate type based on the source</li>
     *   <li>Handle tenant isolation (via SecurityContext)</li>
     * </ul>
     * </p>
     *
     * @param orgId   the organization identifier, must not be null
     * @param contact the contact to save, must not be null
     * @param source  the source of the update (manual or auto-prefill), must not be null
     */
    void saveContact(String orgId, OrganizationContact contact, ContactUpdateSource source);
}
