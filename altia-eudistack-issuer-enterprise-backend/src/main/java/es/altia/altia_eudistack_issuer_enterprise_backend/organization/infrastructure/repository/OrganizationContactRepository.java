package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.repository;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.ContactUpdateSource;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.model.OrganizationContact;
import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Infrastructure adapter for organization contact persistence.
 * <p>
 * Implements the {@link OrganizationContactService} port using blocking JDBC access.
 * Schema-per-tenant isolation is handled transparently by the infrastructure's
 * connection factory configuration.
 * </p>
 * <p>
 * This adapter is responsible only for persistence operations; audit event emission
 * is handled by the application layer (workflows).
 * </p>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class OrganizationContactRepository implements OrganizationContactService {

    private static final String SELECT_CONTACT_EMAIL = """
            SELECT contact_email
            FROM organization
            WHERE id = ?
            """;

    private static final String UPDATE_CONTACT_EMAIL = """
            UPDATE organization
            SET contact_email = ?, updated_at = now()
            WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    /**
     * Finds the contact email for the given organization.
     * <p>
     * Queries the organization table in the current tenant's schema. Returns empty
     * if the organization does not exist or if the contact_email column is NULL.
     * </p>
     *
     * @param orgId the organization identifier, must not be null
     * @return an Optional containing the organization contact if set, or empty if no contact exists
     */
    @Override
    public Optional<OrganizationContact> findContactByOrganizationId(String orgId) {
        log.debug("Finding contact email for organization: {}", orgId);

        try {
            String email = jdbcTemplate.queryForObject(
                    SELECT_CONTACT_EMAIL,
                    String.class,
                    orgId
            );

            if (email == null) {
                log.debug("Contact email is NULL for organization: {}", orgId);
                return Optional.empty();
            }

            log.debug("Found contact email for organization: {}", orgId);
            return Optional.of(new OrganizationContact(email));

        } catch (EmptyResultDataAccessException e) {
            log.debug("Organization not found: {}", orgId);
            return Optional.empty();
        }
    }

    /**
     * Saves or updates the contact email for the given organization.
     * <p>
     * Updates the contact_email column in the organization table. The update also
     * refreshes the updated_at timestamp. If the organization does not exist, the
     * update affects 0 rows (no exception is thrown).
     * </p>
     * <p>
     * Note: This method does NOT emit audit events. Audit event emission is the
     * responsibility of the application layer workflow that invokes this repository.
     * </p>
     *
     * @param orgId   the organization identifier, must not be null
     * @param contact the contact to save, must not be null
     * @param source  the source of the update (manual or auto-prefill), must not be null
     */
    @Override
    public void saveContact(String orgId, OrganizationContact contact, ContactUpdateSource source) {
        log.debug("Saving contact email for organization: {} (source: {})", orgId, source);

        int rowsAffected = jdbcTemplate.update(
                UPDATE_CONTACT_EMAIL,
                contact.email(),
                orgId
        );

        if (rowsAffected == 0) {
            log.warn("No organization found with id: {} - contact not saved", orgId);
        } else {
            log.debug("Successfully saved contact email for organization: {}", orgId);
        }
    }
}
