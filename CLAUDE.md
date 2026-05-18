# Enterprise Issuer — Repo Guide for Claude

> **Per-repo CLAUDE.md.** Loaded only when working inside this repo. The
> SDD Constitution lives in `../eudistack-platform-dev/CLAUDE.md`.

## Identity

Enterprise-tier extension of the EUDIStack Issuer. Wraps
`eudistack-core-issuer` with additional enterprise features
(integration with Altia's tenant `altia-eudistack-issuer-*` backends).

> **Provisioning note:** the `altia` tenant is **NOT** provisioned on
> AWS STG. Smoke tests use `sandbox` / `cgcom` / `kpmg`. See platform-dev
> CLAUDE.md §Project invariants.

## Tech stack

- **Java 25** (Gradle toolchain)
- **Spring Boot 3.5** + WebFlux
- Wraps and extends `eudistack-core-issuer` modules

Two sub-modules visible in repo:

- `altia-eudistack-issuer-core-backend/` — Altia-flavoured core backend
- `altia-eudistack-issuer-enterprise-backend/` — Enterprise add-ons

## Architecture

Same hexagonal discipline as core Issuer. Extension points respect
core ports; never override domain logic — only add adapters.

Strict rules: `../eudistack-platform-dev/.claude/rules/hexagonal-discipline.md`.

## Common commands

> **Dev stack runs in Docker** via `make up` from `eudistack-platform-dev`.

| Task | Command |
|------|---------|
| Compile | `./gradlew compileJava` |
| Tests | `./gradlew test` |
| Full check | `./gradlew check` |
| Rebuild Docker image | `cd ../eudistack-platform-dev && make rebuild-enterprise-issuer` |

## Where to find specs

`../eudistack-platform-dev/docs/EUDISTACK-NNN-*/EUDISTACK-MMM/`.

## Git workflow

- **Squash merge to `main`.** Conventional Commits + Story footer.

## References

- Constitution: [`../eudistack-platform-dev/CLAUDE.md`](../eudistack-platform-dev/CLAUDE.md)
- Core Issuer: [`../eudistack-core-issuer/CLAUDE.md`](../eudistack-core-issuer/CLAUDE.md)
- Skills: `java-spring-hexagonal`, `code-review-checklist`, `commit-conventions`
- Rules: `hexagonal-discipline`, `tenant-isolation`, `protocol-compliance`
