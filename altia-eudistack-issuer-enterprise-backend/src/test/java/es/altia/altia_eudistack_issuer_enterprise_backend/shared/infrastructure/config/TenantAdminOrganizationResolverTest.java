package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TenantAdminOrganizationResolver}.
 *
 * @since EUD-226 (Task 30)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantAdminOrganizationResolver")
class TenantAdminOrganizationResolverTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("getAdminOrganizationId returns the configured value")
    void getAdminOrganizationId_configured_returnsValue() {
        // Given
        when(jdbcTemplate.queryForObject(any(String.class), eq(String.class), eq("admin_organization_id")))
                .thenReturn("VATES-A15456585");
        TenantAdminOrganizationResolver resolver = new TenantAdminOrganizationResolver(jdbcTemplate);

        // When
        Optional<String> result = resolver.getAdminOrganizationId();

        // Then
        assertThat(result).contains("VATES-A15456585");
    }

    @Test
    @DisplayName("getAdminOrganizationId returns empty when the key is not found")
    void getAdminOrganizationId_notFound_returnsEmpty() {
        // Given
        when(jdbcTemplate.queryForObject(any(String.class), eq(String.class), eq("admin_organization_id")))
                .thenThrow(new EmptyResultDataAccessException(1));
        TenantAdminOrganizationResolver resolver = new TenantAdminOrganizationResolver(jdbcTemplate);

        // When
        Optional<String> result = resolver.getAdminOrganizationId();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAdminOrganizationId returns empty when the configured value is blank")
    void getAdminOrganizationId_blankValue_returnsEmpty() {
        // Given
        when(jdbcTemplate.queryForObject(any(String.class), eq(String.class), eq("admin_organization_id")))
                .thenReturn("");
        TenantAdminOrganizationResolver resolver = new TenantAdminOrganizationResolver(jdbcTemplate);

        // When
        Optional<String> result = resolver.getAdminOrganizationId();

        // Then
        assertThat(result).isEmpty();
    }
}
