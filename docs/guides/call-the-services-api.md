---
title: Call the Wanaku services API
description: Use ServicesHttpClient to call Wanaku Barn and the Wanaku management API for data stores, catalogs, templates, tools, and forwards, with OAuth2.
---

# Call the Wanaku services API

`ServicesHttpClient` sends requests to Wanaku Barn and to the Wanaku management API, and returns the results as SDK types. This guide shows how to configure the client, authenticate, and use its operations.

## Prerequisites

- The `capabilities-services-client` module. See [Get started](../get-started/index.md).
- The address of a Wanaku Barn instance, of a Wanaku server, or of both.

## Configure the client

Create a `ServiceConfig` with `DefaultServiceConfig.Builder`, then give it to the client:

```java
ServiceConfig config = DefaultServiceConfig.Builder.newBuilder()
        .baseUrl("http://localhost:8180")
        .serializer(new JacksonSerializer())
        .build();

ServicesHttpClient client = new ServicesHttpClient(config);
```

| Builder method | Value |
|---|---|
| `baseUrl` | The server URL. The client adds the `/api/v1/...` paths to it. A trailing `/` is removed. |
| `serializer` | The JSON serializer. Use `JacksonSerializer`. |
| `clientId` | The OAuth2 client ID. Optional. |
| `secret` | The OAuth2 client secret. Optional. |
| `tokenEndpoint` | The OIDC realm URL, or its `/protocol/openid-connect/token` URL. Optional. |

One client has one base URL. Select it for the operations that you use (see [Use the operations](#use-the-operations)):

- For the Barn operations, use the Barn backend, for example `http://localhost:8180`, or the Wanaku plugin proxy for Barn, for example `http://localhost:8080/api/plugins/wanaku-barn/wanaku-barn-api`.
- For the Wanaku operations, use the Wanaku management API, for example `http://localhost:8080`.

## Authenticate

The client supports two methods.

### OAuth2 client credentials

Set `clientId`, `secret`, and `tokenEndpoint`. When all three have a value, the client creates a `ServiceAuthenticator` (from `capabilities-security`). The authenticator gets an access token with the client credentials grant, renews it before it expires, and adds the `Authorization` header to each request.

```java
ServiceConfig config = DefaultServiceConfig.Builder.newBuilder()
        .baseUrl("http://localhost:8180")
        .serializer(new JacksonSerializer())
        .clientId("wanaku-service")
        .secret(System.getenv("WANAKU_CLIENT_SECRET"))
        .tokenEndpoint("http://keycloak:8080/realms/wanaku")
        .build();
```

The authenticator gets the first token in the client constructor. If the OIDC provider is not available, the constructor fails.

### Bearer token or other headers

Use the constructor with an `HttpClient` and a request customizer. The customizer runs on each request, after the OAuth2 header is added:

```java
HttpClient httpClient = HttpClient.newHttpClient();

ServicesHttpClient client = new ServicesHttpClient(config, httpClient, builder -> builder
        .timeout(Duration.ofSeconds(30))
        .setHeader("Authorization", "Bearer " + token));
```

You own the lifecycle of the `HttpClient` that you give to this constructor.

## Use the operations

Most operations return a `WanakuResponse<T>`. Read the result with `data()`. If the server returns a status outside 2xx, the client throws a `WanakuWebException` (from `capabilities-common`) with the status code. For an I/O or JSON error, the client throws a `WanakuException`.

| Group | Server | Operations | API path |
|---|---|---|---|
| Data stores | Barn | `addDataStore`, `listDataStores`, `getDataStoreById`, `getDataStoresByName`, `removeDataStore`, `removeDataStoresByName` | `/api/v1/data-store` |
| Service catalogs | Barn | `getServiceCatalog` | `/api/v1/service-catalog/download` |
| Service templates | Barn | `listServiceTemplates`, `getServiceTemplate`, `getServiceTemplateProperties`, `downloadServiceTemplate`, `deployServiceTemplate`, `instantiateServiceTemplate`, `removeServiceTemplate` | `/api/v1/service-template/...` |
| Tools | Wanaku | `addTool`, `listTools`, `getToolByName`, `updateTool`, `removeTool` | `/api/v1/tools` |
| Resources | Wanaku | `exposeResource`, `listResources`, `updateResource`, `removeResource` | `/api/v1/resources` |
| Forwards | Wanaku | `addForward`, `listForwards`, `updateForward`, `removeForward` | `/api/v1/forwards` |
| Namespaces | Wanaku | `listNamespaces` | `/api/v1/namespaces` |

`getServiceCatalog` returns a `DataStore`. Its `data` field holds the catalog ZIP archive as Base64 text. To extract it, see [Load service catalogs and routes](load-catalogs-and-routes.md).

## Next steps

- [Load service catalogs and routes](load-catalogs-and-routes.md): extract a catalog and load its Camel routes.
- [API types](../reference/api-types.md): the types that the client returns.
