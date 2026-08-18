package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.model.AuditEventType;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service.AuditService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration providing a no-op AuditService implementation.
 * <p>
 * This configuration is automatically discovered by Spring Boot test slices
 * and full @SpringBootTest contexts when running under the "test" profile.
 * </p>
 * <p>
 * The no-op implementation allows tests to run without requiring a full
 * audit infrastructure (DB, event bus, etc.). Tests that need to verify
 * audit behavior should mock AuditService explicitly with @MockBean.
 * </p>
 *
 * @since EUD-226 (test infrastructure)
 */
@TestConfiguration
public class TestAuditServiceConfiguration {

    /**
     * Provides a no-op AuditService for tests.
     * <p>
     * This bean is marked as @Primary to ensure it takes precedence over any
     * conditional auto-configurations that might be present in the main source.
     * </p>
     */
    @Bean
    @Primary
    public AuditService testAuditService() {
        return new AuditService() {
            @Override
            public void recordOrganizationContactEvent(
                    AuditEventType eventType,
                    String organizationId,
                    String actor,
                    String oldValue,
                    String newValue) {
                // No-op: audit events are not verified in these tests
            }
        };
    }
}
