# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- **EUD-226 — Gestionar el contacto de la organización para notificaciones del ciclo de vida (US-07)**
  - **REST API (FR-17, AC-01, AC-02)**: New `GET /api/v1/organizations/{id}/contact` and `PUT /api/v1/organizations/{id}/contact` endpoints. The GET returns `{ email: string | null }` (200 if found or not found — see known limitation below, 403 if no write capability). The PUT accepts `{ email: string }` with Jakarta `@Email` validation (204 on success, 400 on invalid format, 403 if no write capability, 404 if feature disabled or organization not found).
  - **Database schema (FR-17, AC-06)**: Flyway migration adds `contact_email VARCHAR(255) NULL` column to `organization` table, respecting schema-per-tenant isolation.
  - **Feature flag (FR-17, AC-04)**: New `features.organization_contact.enabled` in `tenant_config` table (default `false`). Exposed via `TenantFeatureFlags.organizationContactEnabled` and `MeResponse.tenantFeatures.organizationContactEnabled`.
  - **Auto-prefill on issuance (FR-18, AC-05)**: `IssuanceServiceImpl` (enterprise-only) silently prefills the organization contact email after successful credential issuance when: (1) feature flag enabled, (2) contact field is empty, (3) session email available. Fire-and-forget (exceptions logged, never fail the issuance). Emits `ORGANIZATION_CONTACT_AUTO_PREFILLED` audit event with `ContactUpdateSource.AUTO_PREFILL`.
  - **Authorization (AC-03) — KNOWN GAP, NOT YET ENFORCED**: `MinimalAuthorizationServiceImpl.canWrite()` is currently a stub returning `true` unconditionally. Caso A (multi-org admin, read-only) is **not yet denied** and org-level isolation is **not yet enforced** — see `/code-review` `quality-report.md` finding B2/F1 (CRITICAL). New endpoints are also **not yet registered in `SecurityConfig`** (finding B1/F2, CRITICAL) — as of this entry they are unreachable in a real deployment via the default deny-all rule. Both must be fixed together before the feature flag can be enabled for any tenant.
  - **Audit trail (AC-02, AC-05) — KNOWN GAP**: `OrganizationContactWorkflow` emits `ORGANIZATION_CONTACT_UPDATED` / `ORGANIZATION_CONTACT_AUTO_PREFILLED` via the `AuditService` port, but no production `AuditService` implementation exists yet (only a test double) — see finding B5/F3 (CRITICAL). The port signature also lacks an actor field, so "quien" (AC-02) cannot be captured even once implemented.
  - **Domain model**: New `OrganizationContact(String email)` record and `ContactUpdateSource { MANUAL, AUTO_PREFILL }` enum. Port `OrganizationContactService` with `findContactByOrganizationId` / `saveContact`.
  - **Infrastructure**: `OrganizationContactRepository` (JDBC adapter), `OrganizationContactWorkflow` (application orchestration), `OrganizationContactController` (REST adapter with `@Email` validation).
  - **Limitations (EC-03, documented)**: Auto-prefill only applies to enterprise-mediated issuance flows (Portal → Enterprise → Core). Direct flows (MFE → Core, REST → Core) do not trigger prefill. JWT parsing for organization ID extraction is blocked (`extractOrganizationId()` unconditionally returns `null`) — auto-prefill is currently dead code end-to-end.
  - **Tests**: Unit tests for workflow (`OrganizationContactWorkflowTest`, 9 scenarios) and integration tests for controller (`OrganizationContactControllerIT`, 8 scenarios covering GET/PUT with 200/204/400/403/404) — note these ITs run with Spring Security filters disabled and mock the authorization service, so they do not exercise the gaps above.
  - **Status:** `/code-review EUD-226` (2026-08-17) returned CHANGES REQUESTED with 5 BLOCKING findings. Feature flag remains `false` by default for all tenants pending fixes. See `docs/EUD-5-gestion-ciclo-vida-portal/EUD-226/quality-report.md`.

### Changed
- Improved GDPR compliance by reducing PII logging.

## [v0.3.0](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.3.0)

### Removed

- Signing configuration logic (`IssuanceConfig`, `SignatureConfig`, `RemoteSignatureConfig`, properties, DTOs, `SigningConfigHttpClient` and `YamlConfigAdapter`). Signing config is now owned by Core and consumed per-tenant from its database.
- `app.signing.*` and `app.remote-signature.*` properties from `application.yaml` / `application-dev.yaml`.

## [v0.2.4](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.2.4)
### Changed
- Close the application during startup if pushing the signing configuration to Core fails.
- Fixed remote signature configuration validation by removing the empty default for signPath.

## [v0.2.3](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.2.3)
### Changed
- Internal refactor (join properties in a single directory, enhance tests)

## [v0.2.2](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.2.2)
### Changed
- Move app-specific properties under `app.*`

## [v0.2.1](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.2.1)
### Added
- Added QTSP Config

## [v0.2.0](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.2.0)
### Added
- Added Authentic Source data acquisition

## [v0.1.0](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.1.0)
### Added
- Added logs configuration

## [v0.0.1](https://github.com/in2workspace/altia-eudistack-issuer-enterprise-backend/releases/tag/v0.0.1)
### Added
- Create project
