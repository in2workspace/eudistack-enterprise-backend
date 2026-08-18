package es.altia.altia_eudistack_issuer_enterprise_backend.shared.domain.service;

import java.util.Optional;

/**
 * Domain port for resolving the identity of the currently authenticated caller.
 * <p>
 * Abstracts how the caller's organization identifier and actor label are obtained
 * from the execution context (e.g. the bearer token attached to the current request)
 * so that domain/application code never depends on Spring Security or JWT libraries.
 * </p>
 * <p>
 * Consumers: {@code MinimalAuthorizationServiceImpl.canWrite()} (EUD-226 T30, resolving
 * the caller's own organization to enforce SoD/org isolation) and the production
 * {@code AuditService} adapter (EUD-226 T31, resolving the actor for the audit trail).
 * </p>
 */
public interface CallerIdentityResolver {

    /**
     * Resolves the organization identifier of the currently authenticated caller.
     *
     * @return the caller's organization identifier, or empty if it cannot be resolved
     *         (no authenticated caller, missing/unparseable claim)
     */
    Optional<String> resolveOrganizationId();

    /**
     * Resolves a stable, loggable label identifying the currently authenticated caller,
     * for use as the {@code actor} in audit events.
     *
     * @return the caller's actor label, or empty if it cannot be resolved
     */
    Optional<String> resolveActor();
}
