# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- **EUD-226 — Gestionar el contacto de la organización para notificaciones del ciclo de vida (US-07)**
  - **REST API (FR-17, AC-01, AC-02)**: New `GET /api/v1/organizations/{id}/contact` and `PUT /api/v1/organizations/{id}/contact` endpoints, registered in `SecurityConfig` with `.authenticated()`. The GET returns `{ email: string | null }` (200 if found or not found — see known limitation below, 403 if no write capability). The PUT accepts `{ email: string }` with Jakarta `@Email` validation (204 on success, 400 on invalid format, 403 if no write capability, 404 if feature disabled or organization not found).
  - **Database schema (FR-17, AC-06)**: Flyway migration adds `contact_email VARCHAR(255) NULL` column to `organization` table, respecting schema-per-tenant isolation.
  - **Feature flag (FR-17, AC-04)**: New `features.organization_contact.enabled` in `tenant_config` table (default `false`). Exposed via `TenantFeatureFlags.organizationContactEnabled` and `MeResponse.tenantFeatures.organizationContactEnabled`.
  - **Auto-prefill on issuance (FR-18, AC-05)**: `IssuanceServiceImpl` (enterprise-only) silently prefills the organization contact email after successful credential issuance when: (1) feature flag enabled, (2) contact field is empty, (3) session email available. Fire-and-forget (exceptions logged, never fail the issuance). Emits `ORGANIZATION_CONTACT_AUTO_PREFILLED` audit event with `ContactUpdateSource.AUTO_PREFILL`. **Known limitation:** organization ID extraction from the issuance session is unimplemented (`extractOrganizationId()` returns `null`), so this path is currently dead code end-to-end — tracked separately, unrelated to the security items below.
  - **Authorization (AC-03, AC-06)**: `MinimalAuthorizationServiceImpl.canWrite()` denies Caso A (multi-org admin, read-only) and cross-organization writes, resolving caller identity from the bearer token's `mandator.organizationIdentifier` claim (`JwtCallerIdentityResolver`) — fails closed when identity cannot be resolved. **Accepted, documented scope boundary:** this repository has no signature-verified JWT infrastructure (no OAuth2 resource server) — the claim is decoded but not cryptographically verified, so the decision logic is correct against a legitimately-issued token but not against a forged one. This is a pre-existing, whole-repo platform gap, not introduced by this Story, and is intentionally out of scope here (EUD-226's purpose is a tender/licitación deliverable, not a production-ready deploy). **`features.organization_contact.enabled` must stay `false` in every environment, and this component must not be exposed to untrusted callers, until a signature-verified resource server exists.** See `docs/EUD-5-gestion-ciclo-vida-portal/EUD-226/tech-debt.md` TD-4.
  - **Audit trail (AC-02, AC-05)**: `JdbcAuditService` persists `ORGANIZATION_CONTACT_UPDATED` / `ORGANIZATION_CONTACT_AUTO_PREFILLED` events (new `audit_event` table) with an `actor` field, resolved via the same caller-identity mechanism above — inherits the same unverified-claim caveat.
  - **Domain model**: New `OrganizationContact(String email)` record and `ContactUpdateSource { MANUAL, AUTO_PREFILL }` enum. Port `OrganizationContactService` with `findContactByOrganizationId` / `saveContact`.
  - **Infrastructure**: `OrganizationContactRepository` (JDBC adapter), `OrganizationContactWorkflow` (application orchestration), `OrganizationContactController` (REST adapter with `@Email` validation), `CallerIdentityResolver`/`JwtCallerIdentityResolver`/`TenantAdminOrganizationResolver` (caller identity resolution).
  - **Tests**: Unit tests for workflow, real (non-mocked) authorization bean, both resolvers, `JdbcAuditService`, and integration tests for the controller with Spring Security filters enabled (401 regression tests included).
  - **Status:** `/code-review EUD-226` (2026-08-18) returned **APPROVED** for this Story's scope (tender/demo deliverable), with a binding non-deployment condition on the feature flag per the accepted scope boundary above. See `docs/EUD-5-gestion-ciclo-vida-portal/EUD-226/quality-report.md`.

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
