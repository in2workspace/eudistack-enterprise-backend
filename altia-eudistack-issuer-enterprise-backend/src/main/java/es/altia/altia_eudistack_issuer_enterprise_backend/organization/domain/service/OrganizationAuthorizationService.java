package es.altia.altia_eudistack_issuer_enterprise_backend.organization.domain.service;

/**
 * Domain port for authorization checks on organization-scoped operations.
 * <p>
 * Defines the contract for verifying whether the current user has the necessary
 * capabilities to perform write operations on a given organization. This port
 * abstracts the underlying authorization mechanism (PBAC, RBAC, etc.) from the
 * domain logic.
 * </p>
 * <p>
 * According to SRS §3.1 (EUD-5), write capability depends on the user's role and
 * the tenant topology:
 * </p>
 * <ul>
 *   <li><b>Caso A (multi-org tenant admin):</b> read-only access — canWrite() returns false</li>
 *   <li><b>Caso B (single-org tenant admin):</b> read-write access — canWrite() returns true</li>
 *   <li><b>Caso C (organization operator):</b> read-write access — canWrite() returns true</li>
 * </ul>
 * <p>
 * This is a minimal port for EUD-226. The full PBAC implementation (ADR-F05) will
 * replace the current stub with a policy decision point that evaluates powers from
 * LEARCredentials. See {@code docs/_shared/architecture/adr/adr-f05-pbac-over-rbac.md}.
 * </p>
 */
public interface OrganizationAuthorizationService {

    /**
     * Checks whether the current user has write capability for the specified organization.
     * <p>
     * Implementations extract the user context from the execution environment
     * (SecurityContext, Reactor Context, etc.) and evaluate authorization rules.
     * </p>
     * <p>
     * For EUD-226, this method enforces segregation of duties (SoD) per NFR-S-01:
     * multi-org tenant admins (Caso A) must not be able to modify organization data,
     * ensuring separation between oversight and operational roles.
     * </p>
     *
     * @param organizationId the organization identifier, must not be null
     * @return {@code true} if the current user can write to the organization, {@code false} otherwise
     */
    boolean canWrite(String organizationId);

    // Future methods for full PBAC (ADR-F05):
    // boolean hasPower(String organizationId, String power);
    // PdpDecision evaluate(OrganizationId orgId, Action action);
}
