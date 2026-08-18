package es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model;

import java.util.Objects;

/**
 * Organization contact email for lifecycle notifications.
 * <p>
 * Value object representing the contact email address of an organization,
 * used for sending notifications about credential lifecycle events.
 * </p>
 *
 * @param email The contact email address. Must not be null.
 */
public record OrganizationContact(String email) {

    /**
     * Compact constructor with validation.
     * Ensures the email field is not null.
     */
    public OrganizationContact {
        Objects.requireNonNull(email, "email must not be null");
    }
}
