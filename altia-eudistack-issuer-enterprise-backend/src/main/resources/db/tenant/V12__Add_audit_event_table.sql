-- =============================================================================
-- V12__Add_audit_event_table.sql
-- Per-tenant migration: creates the audit_event table.
-- EUD-226 (Task 31): persistent audit trail for organization contact changes
-- (AC-02, AC-05 — "quien, cuando, valor anterior, valor nuevo"; SRS NFR-O-01;
-- ENS op.exp.8 / NIS2 traceability / eIDAS 2.0 audit obligations).
-- =============================================================================
CREATE TABLE IF NOT EXISTS audit_event (
    id              BIGSERIAL PRIMARY KEY,
    event_type      VARCHAR(100) NOT NULL,
    organization_id VARCHAR(255) NOT NULL,
    actor           VARCHAR(255),
    old_value       VARCHAR(255),
    new_value       VARCHAR(255),
    occurred_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_audit_event_organization_id ON audit_event (organization_id);
CREATE INDEX IF NOT EXISTS idx_audit_event_occurred_at ON audit_event (occurred_at);

COMMENT ON TABLE audit_event IS
    'Durable audit trail for security-relevant actions (EUD-226: organization contact updates).';
COMMENT ON COLUMN audit_event.actor IS
    'Identifies who made the change (caller organization identifier, or "system" for automatic actions). May be NULL when the actor could not be resolved.';
