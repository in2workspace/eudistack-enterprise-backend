package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for reading tenant-specific feature flags from the tenant_config table.
 * <p>
 * This service provides blocking access to feature configuration stored in the database.
 * Feature flags are queried per-tenant and returned synchronously using JdbcTemplate.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantFeatureFlags {

    private static final String ORGANIZATION_CONTACT_ENABLED_KEY = "features.organization_contact.enabled";
    private static final String QUERY_CONFIG_VALUE = "SELECT config_value FROM tenant_config WHERE config_key = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Checks if the organization contact management feature is enabled for the current tenant.
     * <p>
     * This feature controls whether users can manage organization contact emails
     * for lifecycle notifications (manual edition + auto-prefill from first issuance).
     * </p>
     *
     * @return true if the feature is enabled, false otherwise (including when the key is not found)
     */
    public Boolean isOrganizationContactEnabled() {
        try {
            String value = jdbcTemplate.queryForObject(
                    QUERY_CONFIG_VALUE,
                    String.class,
                    ORGANIZATION_CONTACT_ENABLED_KEY
            );
            return parseBoolean(value);
        } catch (Exception e) {
            // Key not found or query error - default to disabled
            log.debug("Failed to query tenant config for key {}, defaulting to false: {}",
                    ORGANIZATION_CONTACT_ENABLED_KEY, e.getMessage());
            return false;
        }
    }

    /**
     * Parses a configuration value string to a boolean.
     * Accepts "true" (case-insensitive) as true, everything else as false.
     *
     * @param value the configuration value to parse
     * @return true if value is "true" (case-insensitive), false otherwise
     */
    private Boolean parseBoolean(String value) {
        if (value == null) {
            log.debug("Tenant config value is null for key {}, defaulting to false", ORGANIZATION_CONTACT_ENABLED_KEY);
            return false;
        }
        return "true".equalsIgnoreCase(value.trim());
    }
}
