-- =============================================================================
-- V10__Add_organization_contact_email.sql
-- Per-tenant migration: adds organization table and contact_email column.
-- EUD-226: Organization contact management for lifecycle notifications.
-- =============================================================================

-- =============================================================================
-- organization: stores organization metadata per tenant
-- Created conditionally if not exists. Minimal structure for now.
-- =============================================================================
CREATE TABLE IF NOT EXISTS organization (
    id         VARCHAR(255) PRIMARY KEY,  -- Organization identifier (e.g., VATES-A12345678)
    name       VARCHAR(255),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- =============================================================================
-- Add contact_email column for lifecycle notifications
-- =============================================================================
ALTER TABLE organization
    ADD COLUMN IF NOT EXISTS contact_email VARCHAR(255) NULL;

COMMENT ON COLUMN organization.contact_email IS
    'Contact email for lifecycle notifications (auto-filled from first issuance or manually set)';

-- =============================================================================
-- Seed feature flag for organization contact management
-- Default: disabled (false). Will be enabled for specific tenants via seed/update.
-- =============================================================================
INSERT INTO tenant_config (config_key, config_value, description) VALUES
    ('features.organization_contact.enabled', 'false',
     'Enable organization contact management (auto-prefill + manual edition)')
ON CONFLICT (config_key) DO NOTHING;
