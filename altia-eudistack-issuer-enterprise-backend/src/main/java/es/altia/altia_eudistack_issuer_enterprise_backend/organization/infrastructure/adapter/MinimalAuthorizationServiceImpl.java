package es.altia.altia_eudistack_issuer_enterprise_backend.organization.infrastructure.adapter;

import es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service.OrganizationAuthorizationService;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service.CallerIdentityResolver;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantAdminOrganizationResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Minimal PDP-style implementation of {@link OrganizationAuthorizationService} for EUD-226.
 * <p>
 * Resolves the caller's own organization identifier via {@link CallerIdentityResolver}
 * (bearer token claim {@code mandator.organizationIdentifier}) and applies SRS §3.1:
 * </p>
 * <ul>
 *   <li>Caso A (caller's org == tenant's {@code admin_organization_id}): {@code false} (SoD, read-only)</li>
 *   <li>Caso B/C (caller's org == requested {@code organizationId}, not Caso A): {@code true}</li>
 *   <li>Cross-org (caller's org != requested {@code organizationId}): {@code false}</li>
 *   <li>Caller identity cannot be resolved: {@code false} (fail closed)</li>
 * </ul>
 * <p>
 * <b>Scope limitation (EUD-226, quality-report.md B2/F1 remediation):</b> This still is
 * not the full PBAC implementation (ADR-F05) — it deliberately trades PBAC's power-based
 * policy evaluation for a minimal org-identity comparison sufficient to satisfy AC-03,
 * AC-06 and ES-03. It also inherits the "not cryptographically verified" limitation of
 * {@link CallerIdentityResolver}'s current implementation — see that interface's
 * Javadoc and the tech-debt entry tracking the full OAuth2 resource-server buildout.
 * </p>
 * <p>
 * See {@code docs/_shared/architecture/adr/adr-f05-pbac-over-rbac.md} for the full
 * authorization model.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinimalAuthorizationServiceImpl implements OrganizationAuthorizationService {

    private final CallerIdentityResolver callerIdentityResolver;
    private final TenantAdminOrganizationResolver tenantAdminOrganizationResolver;

    /**
     * Checks whether the current caller has write capability for the specified organization.
     *
     * @param organizationId the organization identifier from the request path
     * @return {@code true} only for Caso B/C callers acting on their own organization
     */
    @Override
    public boolean canWrite(String organizationId) {
        Optional<String> callerOrganizationId = callerIdentityResolver.resolveOrganizationId();

        if (callerOrganizationId.isEmpty()) {
            log.warn("Denying write access: caller organization identifier could not be resolved");
            return false;
        }

        String callerOrgId = callerOrganizationId.get();

        Optional<String> adminOrganizationId = tenantAdminOrganizationResolver.getAdminOrganizationId();
        if (adminOrganizationId.isPresent() && adminOrganizationId.get().equals(callerOrgId)) {
            log.warn("Denying write access for multi-org tenant admin (Caso A, SoD): callerOrg={}", callerOrgId);
            return false;
        }

        if (!callerOrgId.equals(organizationId)) {
            log.warn("Denying cross-org write attempt: callerOrg={}, requestedOrg={}", callerOrgId, organizationId);
            return false;
        }

        log.debug("Permitting write access for organizationId: {}", organizationId);
        return true;
    }
}
