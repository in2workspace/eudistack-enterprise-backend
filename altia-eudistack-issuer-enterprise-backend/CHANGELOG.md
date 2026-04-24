# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
