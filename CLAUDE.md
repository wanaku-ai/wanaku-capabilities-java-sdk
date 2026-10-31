# Wanaku Capabilities Java SDK

Last updated: 2026-07-17

Plain-Java SDK for building new capabilities for the [Wanaku MCP Router](https://wanaku.ai). Related main project: [wanaku-ai/wanaku](https://github.com/wanaku-ai/wanaku).

## Core Guidelines

- Think before you write
- Don't create abstractions unnecessarily
- Simplicity is important: focus on the minimum code required to achieve the result
- Always run the static analysis profile before submitting work

## Build & Test

- **Build tool:** Maven (no wrapper — use `mvn`)
- **Build:** `mvn clean install`
- **Test:** `mvn test`
- **Format:** Spotless with Palantir Java Format runs automatically during `compile` phase
- **Static analysis:** `mvn verify -Pstatic-analysis` (PMD + SpotBugs)
- **Coverage:** `mvn verify -Pcoverage` (JaCoCo aggregate report)
- **Module-specific build:** yes — run `mvn` in the changed module directory when possible
- **Java version:** 21

## Modules

- `capabilities-parent` — shared dependency management and plugin configuration
- `capabilities-common` — common utilities
- `capabilities-api` — public API (does not need tests)
- `capabilities-security` — OAuth2/OIDC security (Nimbus SDK)
- `capabilities-runtimes` — runtime implementations (Camel runtime, common runtime)
- `capabilities-services-client` — service client
- `capabilities-code-execution-engines` — LangChain4j code execution integration
- `capabilities-maven-downloader` — Maven artifact resolution
- `capabilities-bom` — Bill of Materials

## Active Technologies

- Java 21, Apache Camel, Jackson, JGit, LangChain4j
- Testing: JUnit Jupiter, Mockito
- Code quality: Spotless (Palantir format), PMD, SpotBugs, JaCoCo

## Code Style

- Spotless auto-formats on compile (Palantir Java Format)
- Import order: `jakarta|javax`, `org.w3c|org.xml`, `java|org|io|`, then everything else; wildcards last
- Unused imports are removed automatically
- Wildcard imports are forbidden

## Documentation

- User documentation lives in `docs/`. Follow `docs/contributing/documentation.md`.
- Add each new page to `docs/nav.json`. When you move or rename a page, add a line to `docs/redirects.txt`.
- Run `python3 .github/check-docs-nav.py` and `npx markdownlint-cli2` before you push.
