---
title: Wanaku Capabilities Java SDK
description: Java libraries shared by Wanaku Barn, the Camel Integration Capability, and the Wanaku Semantic Router, with API types, clients, and runtimes.
---

# Wanaku Capabilities Java SDK

The Wanaku Capabilities Java SDK is the set of plain-Java libraries that the Java components of [Wanaku](https://wanaku.ai) share. Wanaku Barn, the Camel Integration Capability (CIC), and the Wanaku Semantic Router (WSR) use it. It contains the shared API types, a client for the Wanaku Barn and Wanaku management APIs, OAuth2 client-credentials helpers, a Camel runtime, a Maven artifact downloader, and a BOM (Bill of Materials) that aligns the versions.

> [!NOTE]
> This documentation covers the `main` branch. Version 0.3.0 removed the gRPC, discovery, data-file, configuration-provider, and archetype modules. For the 0.2.x line, see the [Wanaku Capabilities Java SDK 0.2.x documentation](https://github.com/wanaku-ai/wanaku-capabilities-java-sdk/tree/0.2.x/docs).

## Who uses the SDK

| Component | Modules |
|---|---|
| [Wanaku Barn](https://github.com/wanaku-ai/wanaku-barn) | `capabilities-api`, `capabilities-common`, `capabilities-security` |
| [Camel Integration Capability](https://github.com/wanaku-ai/camel-integration-capability) | `capabilities-common`, `capabilities-services-client`, `capabilities-runtimes-camel-common`, `capabilities-maven-downloader` |
| [Wanaku Semantic Router](https://github.com/wanaku-ai/wanaku-semantic-router) | `capabilities-api`, `capabilities-common`, `capabilities-services-client`, `capabilities-runtimes-camel-common`, `capabilities-maven-downloader` |

## Sections

| Section | Read it to |
|---|---|
| [Get started](get-started/index.md) | Add the SDK to a Maven project and make a first call. |
| [Guides](guides/index.md) | Call the Wanaku services API, load service catalogs and Camel routes, and download Maven artifacts. |
| [Reference](reference/index.md) | Look up the published modules and the API types. |
| [Contributing](contributing/index.md) | Build, test, document, and release the SDK. |

## Next steps

- [Get started](get-started/index.md): add the SDK to your project.
- [Modules](reference/modules.md): find the module that you need.
