package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TenantFeatureFlags;
import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.controller.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing tenant-level configuration and user context to the frontend.
 * <p>
 * The /me endpoint provides runtime feature flags and user-specific settings
 * that control frontend behavior and feature visibility on a per-tenant basis.
 * </p>
 * <p>
 * This is an infrastructure adapter (driving side) that queries tenant configuration
 * and translates it into a DTO consumable by frontend applications.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final TenantFeatureFlags tenantFeatureFlags;

    /**
     * Returns tenant-level feature flags and user context for the current session.
     * <p>
     * Exposes runtime configuration that controls frontend feature visibility,
     * including whether the organization contact management feature is enabled
     * for the current tenant.
     * </p>
     * <p>
     * No explicit authorization required — relies on the tenant context already
     * resolved by the infrastructure filter (X-Tenant-Id header). The endpoint
     * is accessible to any authenticated user within their tenant scope.
     * </p>
     *
     * @return MeResponse containing tenant feature flags
     */
    @GetMapping
    public MeResponse me() {
        log.debug("Fetching tenant feature flags for /me endpoint");

        Boolean organizationContactEnabled = tenantFeatureFlags.isOrganizationContactEnabled();

        log.debug("Tenant feature flags retrieved: organizationContactEnabled={}", organizationContactEnabled);

        return MeResponse.build(organizationContactEnabled);
    }
}
