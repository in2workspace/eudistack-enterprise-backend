package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.repository;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model.AuditEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JdbcAuditService} — the real production {@code AuditService} bean.
 * <p>
 * Prior to EUD-226 Task 31, no production implementation of this port existed at all
 * (quality-report.md B5/F3); only a test no-op ({@code TestAuditServiceConfiguration})
 * was available.
 * </p>
 *
 * @since EUD-226 (Task 31)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JdbcAuditService")
class JdbcAuditServiceTest {

    private static final String INSERT_SQL = """
            INSERT INTO audit_event (event_type, organization_id, actor, old_value, new_value)
            VALUES (?, ?, ?, ?, ?)
            """;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcAuditService auditService;

    @Test
    @DisplayName("recordOrganizationContactEvent persists event with all fields (AC-02 'quien, cuando, valor anterior, valor nuevo')")
    void recordOrganizationContactEvent_persistsAllFields() {
        // Given
        when(jdbcTemplate.update(eq(INSERT_SQL), eq("ORGANIZATION_CONTACT_UPDATED"), eq("org-123"),
                eq("VATES-A15456585"), eq("old@example.com"), eq("new@example.com")))
                .thenReturn(1);

        // When
        auditService.recordOrganizationContactEvent(
                AuditEventType.ORGANIZATION_CONTACT_UPDATED,
                "org-123",
                "VATES-A15456585",
                "old@example.com",
                "new@example.com"
        );

        // Then
        verify(jdbcTemplate).update(INSERT_SQL, "ORGANIZATION_CONTACT_UPDATED", "org-123",
                "VATES-A15456585", "old@example.com", "new@example.com");
    }

    @Test
    @DisplayName("recordOrganizationContactEvent persists a null actor when it could not be resolved")
    void recordOrganizationContactEvent_nullActor_persistsNull() {
        // When
        auditService.recordOrganizationContactEvent(
                AuditEventType.ORGANIZATION_CONTACT_AUTO_PREFILLED,
                "org-123",
                null,
                null,
                "new@example.com"
        );

        // Then
        verify(jdbcTemplate).update(INSERT_SQL, "ORGANIZATION_CONTACT_AUTO_PREFILLED", "org-123",
                null, null, "new@example.com");
    }
}
