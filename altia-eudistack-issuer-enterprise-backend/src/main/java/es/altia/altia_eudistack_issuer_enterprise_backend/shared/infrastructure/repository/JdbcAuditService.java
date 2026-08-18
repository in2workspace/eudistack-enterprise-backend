package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.repository;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model.AuditEventType;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Infrastructure adapter that persists audit events to the {@code audit_event} table.
 * <p>
 * Implements the {@link AuditService} port using blocking JDBC access, mirroring the
 * persistence style already used by {@code OrganizationContactRepository} in this
 * codebase. Schema-per-tenant isolation is handled by the infrastructure's connection
 * configuration, consistent with the rest of this repository.
 * </p>
 * <p>
 * EUD-226 (Task 31): replaces the previous state where no production {@link AuditService}
 * implementation existed — only a test no-op ({@code TestAuditServiceConfiguration}) —
 * which meant the audit trail required by AC-02/AC-05 was never actually persisted
 * (quality-report.md B5/F3).
 * </p>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcAuditService implements AuditService {

    private static final String INSERT_AUDIT_EVENT = """
            INSERT INTO audit_event (event_type, organization_id, actor, old_value, new_value)
            VALUES (?, ?, ?, ?, ?)
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void recordOrganizationContactEvent(
            AuditEventType eventType,
            String organizationId,
            String actor,
            String oldValue,
            String newValue) {

        log.debug("Persisting audit event: {} for organization: {} (actor: {})", eventType, organizationId, actor);

        jdbcTemplate.update(
                INSERT_AUDIT_EVENT,
                eventType.name(),
                organizationId,
                actor,
                oldValue,
                newValue
        );

        log.info("Audit event persisted: {} for organization: {}", eventType, organizationId);
    }
}
