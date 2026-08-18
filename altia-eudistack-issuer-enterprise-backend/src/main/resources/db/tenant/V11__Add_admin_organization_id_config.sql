-- =============================================================================
-- V11__Add_admin_organization_id_config.sql
-- Per-tenant migration: seeds the admin_organization_id tenant_config key.
-- EUD-226 (Task 30): identifies the multi-org tenant admin organization (Caso A
-- per SRS §3.1) so MinimalAuthorizationServiceImpl.canWrite() can deny write
-- access for SoD (AC-03, AC-06, ES-03).
-- =============================================================================

-- No default value is seeded here: each tenant must be configured explicitly with
-- its admin organization identifier once known. Absence of this key means
-- TenantAdminOrganizationResolver.getAdminOrganizationId() returns empty, and
-- Caso A detection is effectively skipped (fail-open on unknown admin org, but
-- cross-org isolation still applies independently).
INSERT INTO tenant_config (config_key, config_value, description) VALUES
    ('admin_organization_id', '',
     'Organization identifier of this tenant''s multi-org administrator (Caso A, SoD read-only)')
ON CONFLICT (config_key) DO NOTHING;
