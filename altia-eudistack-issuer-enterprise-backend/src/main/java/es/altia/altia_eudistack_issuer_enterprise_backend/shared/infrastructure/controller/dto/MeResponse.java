package es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for the /me endpoint that exposes tenant-level configuration to the frontend.
 * <p>
 * This response provides frontend applications with runtime configuration flags
 * that control feature visibility and behavior on a per-tenant basis.
 * </p>
 */
public record MeResponse(
        @JsonProperty("tenantFeatures") TenantFeaturesDTO tenantFeatures
) {

    /**
     * Nested DTO containing tenant-specific feature flags.
     * <p>
     * Structured as a nested object to allow future expansion of tenant-level
     * configuration without breaking existing clients.
     * </p>
     */
    public record TenantFeaturesDTO(
            @JsonProperty("organizationContactEnabled") Boolean organizationContactEnabled
    ) {
        /**
         * Compact constructor for validation.
         * Ensures the organizationContactEnabled flag is never null.
         */
        public TenantFeaturesDTO {
            if (organizationContactEnabled == null) {
                organizationContactEnabled = false;
            }
        }
    }

    /**
     * Factory method to build a MeResponse from feature flags.
     *
     * @param organizationContactEnabled whether the organization contact management feature is enabled
     * @return a MeResponse with the provided feature flags
     */
    public static MeResponse build(Boolean organizationContactEnabled) {
        return new MeResponse(
                new TenantFeaturesDTO(organizationContactEnabled)
        );
    }
}
