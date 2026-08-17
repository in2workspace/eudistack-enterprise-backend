package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * HTTP request DTO for updating organization contact information.
 * <p>
 * This DTO carries the new contact email address to be set for an organization.
 * Validation is enforced at the HTTP boundary via Jakarta Bean Validation.
 * </p>
 * <p>
 * JSON representation:
 * <pre>
 * {
 *   "email": "new-contact@organization.example"
 * }
 * </pre>
 * </p>
 * <p>
 * Validation rules (ES-01):
 * <ul>
 *   <li>email MUST be present (not null)</li>
 *   <li>email MUST be a valid email format (RFC 5322 simplified)</li>
 * </ul>
 * </p>
 * <p>
 * The request is translated to a domain {@link OrganizationContact} value object
 * via {@link #toDomain()} for consumption by the application layer.
 * </p>
 *
 * @param email The new contact email address, must not be null and must be valid email format
 */
public record UpdateOrganizationContactRequest(
        @NotNull(message = "email must not be null")
        @Email(message = "email must be a valid email address")
        @JsonProperty("email") String email
) {

    /**
     * Converts this request DTO to a domain OrganizationContact value object.
     * <p>
     * This method assumes validation has already been performed by the framework
     * at the controller boundary (via @Valid annotation).
     * </p>
     *
     * @return a new OrganizationContact instance
     */
    public OrganizationContact toDomain() {
        return new OrganizationContact(email);
    }
}
