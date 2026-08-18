package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for reading the tenant's admin organization identifier from the tenant_config table.
 * <p>
 * The admin organization identifies the multi-org tenant admin (Caso A per SRS §3.1):
 * a caller whose organizationIdentifier equals this value is a tenant-wide administrator
 * with read-only access to organization-scoped operations (segregation of duties).
 * </p>
 * <p>
 * Mirrors the {@link TenantFeatureFlags} pattern (blocking JDBC read against the
 * tenant-scoped {@code tenant_config} table). EUD-226 T30.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantAdminOrganizationResolver {

    private static final String ADMIN_ORGANIZATION_ID_KEY = "admin_organization_id";
    private static final String QUERY_CONFIG_VALUE = "SELECT config_value FROM tenant_config WHERE config_key = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Returns the current tenant's admin organization identifier, if configured.
     *
     * @return the admin organization identifier, or empty if not configured or on query error
     */
    public Optional<String> getAdminOrganizationId() {
        try {
            String value = jdbcTemplate.queryForObject(
                    QUERY_CONFIG_VALUE,
                    String.class,
                    ADMIN_ORGANIZATION_ID_KEY
            );
            return (value == null || value.isBlank()) ? Optional.empty() : Optional.of(value);
        } catch (Exception e) {
            log.debug("Failed to query tenant config for key {}: {}", ADMIN_ORGANIZATION_ID_KEY, e.getMessage());
            return Optional.empty();
        }
    }
}
