package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.adapter;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service.CallerIdentityResolver;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantAdminOrganizationResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Direct unit tests for {@link MinimalAuthorizationServiceImpl} — the real production bean,
 * not a mock of {@code OrganizationAuthorizationService}.
 * <p>
 * Prior to EUD-226 Task 30, this class had zero direct test coverage: every other test
 * mocked the interface, which could not detect that the production implementation was a
 * hardcoded {@code return true} (quality-report.md B2/F1/F8).
 * </p>
 *
 * @since EUD-226 (Task 30)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MinimalAuthorizationServiceImpl")
class MinimalAuthorizationServiceImplTest {

    private static final String ORG_ID = "VATES-A15456585";
    private static final String OTHER_ORG_ID = "VATES-B99999999";

    @Mock
    private CallerIdentityResolver callerIdentityResolver;

    @Mock
    private TenantAdminOrganizationResolver tenantAdminOrganizationResolver;

    @InjectMocks
    private MinimalAuthorizationServiceImpl authorizationService;

    @Test
    @DisplayName("canWrite denies Caso A (multi-org tenant admin) even for its own organization (AC-03, ES-03)")
    void canWrite_casoA_returnsFalse() {
        // Given
        when(callerIdentityResolver.resolveOrganizationId()).thenReturn(Optional.of(ORG_ID));
        when(tenantAdminOrganizationResolver.getAdminOrganizationId()).thenReturn(Optional.of(ORG_ID));

        // When
        boolean result = authorizationService.canWrite(ORG_ID);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canWrite denies cross-org write attempt (AC-06)")
    void canWrite_crossOrg_returnsFalse() {
        // Given
        when(callerIdentityResolver.resolveOrganizationId()).thenReturn(Optional.of(ORG_ID));
        when(tenantAdminOrganizationResolver.getAdminOrganizationId()).thenReturn(Optional.empty());

        // When
        boolean result = authorizationService.canWrite(OTHER_ORG_ID);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canWrite permits Caso B/C caller acting on their own organization (EC-04)")
    void canWrite_sameOrgNotAdmin_returnsTrue() {
        // Given
        when(callerIdentityResolver.resolveOrganizationId()).thenReturn(Optional.of(ORG_ID));
        when(tenantAdminOrganizationResolver.getAdminOrganizationId()).thenReturn(Optional.empty());

        // When
        boolean result = authorizationService.canWrite(ORG_ID);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("canWrite permits Caso B/C caller when a different tenant admin org is configured")
    void canWrite_sameOrgDifferentAdminConfigured_returnsTrue() {
        // Given
        when(callerIdentityResolver.resolveOrganizationId()).thenReturn(Optional.of(ORG_ID));
        when(tenantAdminOrganizationResolver.getAdminOrganizationId()).thenReturn(Optional.of(OTHER_ORG_ID));

        // When
        boolean result = authorizationService.canWrite(ORG_ID);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("canWrite denies when caller organization identifier cannot be resolved (fail closed)")
    void canWrite_unresolvableCaller_returnsFalse() {
        // Given
        when(callerIdentityResolver.resolveOrganizationId()).thenReturn(Optional.empty());

        // When
        boolean result = authorizationService.canWrite(ORG_ID);

        // Then
        assertThat(result).isFalse();
    }
}
