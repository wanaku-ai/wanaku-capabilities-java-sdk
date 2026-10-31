---
title: API types
description: The packages, types, and exceptions in the capabilities-api module of the Wanaku Capabilities Java SDK, with the purpose of each type.
---

# API types

The `capabilities-api` module has the types that Wanaku components exchange, and the exceptions that they throw. The base package is `ai.wanaku.capabilities.sdk.api`.

## Responses

| Type | Purpose |
|---|---|
| `types.WanakuResponse<T>` | A record with `error()` (a `WanakuError`) or `data()` (the result). Most `ServicesHttpClient` operations return it. |
| `types.WanakuError` | An error message from a Wanaku API. |
| `types.management.ServerInfo` | Server information, such as the version. |

## Entities

| Type | Purpose |
|---|---|
| `types.WanakuEntity` | Base interface for all entity types. |
| `types.LabelsAwareEntity` | Abstract base class for entities with labels. |
| `types.NameNamespacePair` | A name and a namespace that identify an entity. |
| `types.Namespace` | A Wanaku namespace. |
| `types.ToolReference` | A tool, with name, description, URI, type, and input schema. |
| `types.RemoteToolReference` | A tool on a remote MCP server. |
| `types.CallableReference` | An entity that can be called with arguments. |
| `types.InputSchema` | The input schema of a tool: type, properties, and required fields. |
| `types.Property` | One property of an input schema. |
| `types.ResourceReference` | A resource, with location, type, and parameters. |
| `types.ForwardReference` | A forward to a downstream MCP server. |
| `types.PromptReference` | An MCP prompt. |
| `types.PromptMessage` | A message in an MCP prompt. |
| `types.PromptContent` | Base interface for prompt content. |
| `types.TextContent`, `types.ImageContent`, `types.AudioContent`, `types.EmbeddedResource` | The content kinds of a prompt message. |
| `types.DataStore` | A Barn data store entry: `id`, `name`, and `data` (Base64 text). Barn also returns service catalogs as `DataStore`. |

## Service templates

| Type | Purpose |
|---|---|
| `types.ServiceTemplateSummary` | One template in the template list. |
| `types.ServiceTemplateDetail` | The details of one template. |
| `types.ServiceTemplateSystem` | The files of one system in a template. |
| `types.io.TemplateInstantiationRequest` | The request to make a service catalog from a template. |

## Code execution

The `types.execution` package has the request and event types of the code execution API: `CodeExecutionRequest`, `CodeExecutionResponse`, `CodeExecutionTask`, `CodeExecutionStatus`, `CodeExecutionEvent`, `CodeExecutionEventType`, and `CodeExecutionError`.

> [!WARNING]
> Wanaku and Wanaku Barn on `main` do not serve the code execution API (`/api/v2/code-execution-engine`).

## Service discovery

The `discovery` and `types.discovery` packages (`RegistrationManager`, `DiscoveryCallback`, `ServiceState`, `HealthStatus`, `ActivityRecord`, `StandardMessages`) and the `types.providers` package (`ServiceTarget`, `ServiceType`) describe service registration. The SDK has no implementation of `RegistrationManager` on `main`.

## Utilities

| Type | Purpose |
|---|---|
| `util.ReservedArgumentNames` | Argument names that the Wanaku tool call protocol reserves. |

## Exceptions

All exceptions extend `exceptions.WanakuException`.

| Exception | Thrown when |
|---|---|
| `ConfigurationNotFoundException` | An expected configuration is not found. |
| `DataStoreResourceNotFoundException` | A data store entry does not exist. |
| `EntityAlreadyExistsException` | An entity with the same identity already exists. |
| `InvalidResponseTypeException` | A response has a type other than the expected type. |
| `NamespaceNotFoundException` | A namespace does not exist. |
| `NonConvertableResponseException` | A response cannot be converted to the required type. |
| `PromptNotFoundException` | A prompt does not exist. |
| `ResourceNotFoundException` | A resource does not exist. |
| `ServiceNotFoundException` | A service does not exist. |
| `ServiceUnavailableException` | A service is not available. |
| `ToolNotFoundException` | A tool does not exist. |

## Next steps

- [Modules](modules.md): the other SDK modules.
- [Call the Wanaku services API](../guides/call-the-services-api.md): the client that returns these types.
