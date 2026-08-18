package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;

/**
 * HTTP response DTO for organization contact information.
 * <p>
 * Exposes the contact email address for a given organization. The email may be
 * null if no contact has been configured yet.
 * </p>
 * <p>
 * JSON representation:
 * <pre>
 * {
 *   "email": "contact@organization.example" | null
 * }
 * </pre>
 * </p>
 *
 * @param email The organization contact email address, or null if not set
 */
public record OrganizationContactResponse(
        @JsonProperty("email") String email
) {

    /**
     * Factory method to build a response from a domain OrganizationContact.
     *
     * @param contact the domain contact object
     * @return a new OrganizationContactResponse
     */
    public static OrganizationContactResponse from(OrganizationContact contact) {
        return new OrganizationContactResponse(contact.email());
    }

    /**
     * Factory method to build a response indicating no contact is set.
     *
     * @return a new OrganizationContactResponse with null email
     */
    public static OrganizationContactResponse empty() {
        return new OrganizationContactResponse(null);
    }
}
