---
title: Modules
description: The Maven modules of the Wanaku Capabilities Java SDK, with the coordinates, purpose, and main packages and classes of each published artifact.
---

# Modules

The SDK publishes its artifacts under the `ai.wanaku.sdk` group ID. Import `capabilities-bom` to align their versions, then add the modules that you need.

## Published artifacts

| Artifact | Purpose | Main packages and classes |
|---|---|---|
| `capabilities-bom` | Bill of Materials. Sets the version of each artifact on this page. | – |
| `capabilities-api` | Shared types and exceptions for Wanaku entities: tools, resources, prompts, forwards, namespaces, data stores, service templates, and code execution. | `ai.wanaku.capabilities.sdk.api.types`, `...api.exceptions`, `...api.discovery` (`RegistrationManager`, `DiscoveryCallback`) |
| `capabilities-common` | Shared utilities and service configuration. | `ServicesHelper`, `ProcessRunner`, `VersionHelper`, `config.ServiceConfig`, `serializer.JacksonSerializer` |
| `capabilities-security` | OAuth2 client-credentials authentication with token renewal. | `ServiceAuthenticator`, `TokenEndpoint` |
| `capabilities-services-client` | HTTP client for the Wanaku services API: tools, resources, forwards, namespaces, and service templates. | `ServicesHttpClient` |
| `capabilities-maven-downloader` | Resolves and downloads Maven artifacts. | `WanakuMavenDownloader`, `GAV`, `Repository` |
| `capabilities-runtimes-camel-common` | Camel runtime support: download routes, rules, dependencies, and service catalogs, initialize from Git, and load routes. | `downloader.*`, `init.GitInitializer`, `util.WanakuRoutesLoader` |
| `capabilities-runtimes-camel-plugin` | Configuration for the Camel Integration Capability plugin, from `camel-integration-capability.properties` and environment variables. | `PluginConfiguration` |
| `capabilities-runtimes-common` | Placeholder for runtime-neutral code. It has no classes on `main`. | – |
| `langchain4j-code-execution-engine-wanaku` | A LangChain4j code execution engine that sends code to Wanaku. | `ai.wanaku.capabilities.cee.langchain4j.WanakuCodeExecutionEngine` |
| `capabilities-archetypes-java-tool` | Maven archetype for a new project. | – |

The `capabilities-runtime-camel-common` and `capabilities-runtime-camel-plugin` artifacts (with `runtime`, not `runtimes`) are relocation POMs. They point to the `capabilities-runtimes-camel-*` artifacts.

## Source layout

| Directory | Artifacts |
|---|---|
| `capabilities-parent` | Parent POM with the shared dependency and plugin configuration. Not for direct use. |
| `capabilities-runtimes/capabilities-runtimes-camel` | `capabilities-runtimes-camel-common`, `capabilities-runtimes-camel-plugin`, and the relocation POMs. |
| `capabilities-code-execution-engines` | `langchain4j-code-execution-engine-wanaku`. |
| `capabilities-archetypes` | `capabilities-archetypes-java-tool`. |

## Next steps

- [Get started](../get-started/index.md): add the SDK to your project.
- [Contributing](../contributing/index.md): build the modules from source.
