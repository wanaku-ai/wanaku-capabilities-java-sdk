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
| `capabilities-api` | Shared types and exceptions for Wanaku entities: tools, resources, prompts, forwards, namespaces, data stores, service templates, and code execution. See [API types](api-types.md). | `ai.wanaku.capabilities.sdk.api.types`, `...api.exceptions` |
| `capabilities-common` | Shared utilities, service configuration, and the JSON serializer. | `config.ServiceConfig`, `config.DefaultServiceConfig`, `serializer.JacksonSerializer`, `exceptions.WanakuWebException`, `ServicesHelper`, `ProcessRunner`, `VersionHelper` |
| `capabilities-security` | OAuth2 client-credentials authentication with token renewal. See [Call the Wanaku services API](../guides/call-the-services-api.md). | `ServiceAuthenticator`, `TokenEndpoint` |
| `capabilities-services-client` | HTTP client for Wanaku Barn (data stores, service catalogs, service templates) and the Wanaku management API (tools, resources, forwards, namespaces). See [Call the Wanaku services API](../guides/call-the-services-api.md). | `ServicesHttpClient` |
| `capabilities-maven-downloader` | Resolves and downloads Maven artifacts at runtime. See [Download Maven artifacts](../guides/download-maven-artifacts.md). | `WanakuMavenDownloader`, `GAV`, `Repository` |
| `capabilities-runtimes-camel-common` | Camel runtime support: get routes, dependencies, and service catalogs, clone a Git repository, and load routes. See [Load service catalogs and routes](../guides/load-catalogs-and-routes.md). | `downloader.*`, `init.InitializerFactory`, `util.WanakuRoutesLoader` |
| `capabilities-runtimes-camel-plugin` | Configuration for the Camel Integration Capability plugin, from `camel-integration-capability.properties` and environment variables. | `PluginConfiguration` |
| `capabilities-runtimes-common` | Placeholder for runtime-neutral code. It has no classes on `main`. | – |
| `langchain4j-code-execution-engine-wanaku` | A LangChain4j `CodeExecutionEngine` that sends code to the code execution API. Wanaku and Wanaku Barn on `main` do not serve this API. | `ai.wanaku.capabilities.cee.langchain4j.WanakuCodeExecutionEngine` |

The `capabilities-runtime-camel-common` and `capabilities-runtime-camel-plugin` artifacts (with `runtime`, not `runtimes`) are relocation POMs. They point to the `capabilities-runtimes-camel-*` artifacts.

## Module dependencies

| Artifact | Depends on |
|---|---|
| `capabilities-common` | `capabilities-api` |
| `capabilities-security` | `capabilities-common` |
| `capabilities-services-client` | `capabilities-common`, `capabilities-security` |
| `capabilities-maven-downloader` | `capabilities-api` |
| `capabilities-runtimes-camel-common` | `capabilities-common`, `capabilities-runtimes-common`, `capabilities-services-client` |
| `capabilities-runtimes-camel-plugin` | `capabilities-runtimes-camel-common` |
| `langchain4j-code-execution-engine-wanaku` | `capabilities-services-client`, `capabilities-api` |

## Source layout

| Directory | Artifacts |
|---|---|
| `capabilities-parent` | Parent POM with the shared dependency and plugin configuration. Not for direct use. |
| `capabilities-runtimes/capabilities-runtimes-camel` | `capabilities-runtimes-camel-common`, `capabilities-runtimes-camel-plugin`, and the relocation POMs. |
| `capabilities-code-execution-engines` | `langchain4j-code-execution-engine-wanaku`. |

## Next steps

- [Get started](../get-started/index.md): add the SDK to your project.
- [Contributing](../contributing/index.md): build the modules from source.
