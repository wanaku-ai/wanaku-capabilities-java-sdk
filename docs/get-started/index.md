---
title: Get started
description: Add the Wanaku Capabilities Java SDK to a Maven project with the BOM, select the modules, and read a data store from Wanaku Barn.
---

# Get started

This page shows how to add the Wanaku Capabilities Java SDK to a Maven project and make a first call to Wanaku Barn.

## Prerequisites

- Java 21 or later.
- Maven 3.9 or later.
- A running Wanaku Barn instance for the example call. See the [Wanaku Barn documentation](https://wanaku.ai/docs/barn/next/).

## Add the BOM

All SDK artifacts use the `ai.wanaku.sdk` group ID. Import `capabilities-bom` in `dependencyManagement`. The BOM sets the version of each SDK artifact.

```xml
<properties>
    <wanaku-sdk.version>0.3.0</wanaku-sdk.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>ai.wanaku.sdk</groupId>
            <artifactId>capabilities-bom</artifactId>
            <version>${wanaku-sdk.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Set `wanaku-sdk.version` to the SDK release that you use. The `main` branch builds `0.3.0-SNAPSHOT`.

## Select the modules

Add only the modules that you need. Do not set a version: the BOM supplies it.

| To do this | Add |
|---|---|
| Use the shared types and exceptions | `capabilities-api` |
| Call Wanaku Barn or the Wanaku management API | `capabilities-services-client` |
| Get OAuth2 tokens with client credentials | `capabilities-security` |
| Load service catalogs and Camel routes | `capabilities-runtimes-camel-common` |
| Download Maven artifacts at runtime | `capabilities-maven-downloader` |

```xml
<dependencies>
    <dependency>
        <groupId>ai.wanaku.sdk</groupId>
        <artifactId>capabilities-services-client</artifactId>
    </dependency>
</dependencies>
```

The [Modules](../reference/modules.md) reference lists all artifacts and their dependencies on each other.

## Read a data store

This example connects to Barn and lists the data store entries:

```java
import ai.wanaku.capabilities.sdk.api.types.DataStore;
import ai.wanaku.capabilities.sdk.api.types.WanakuResponse;
import ai.wanaku.capabilities.sdk.common.config.DefaultServiceConfig;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.common.serializer.JacksonSerializer;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;
import java.util.List;

public class ListDataStores {
    public static void main(String[] args) {
        ServiceConfig config = DefaultServiceConfig.Builder.newBuilder()
                .baseUrl("http://localhost:8180")
                .serializer(new JacksonSerializer())
                .build();

        ServicesHttpClient client = new ServicesHttpClient(config);
        WanakuResponse<List<DataStore>> response = client.listDataStores();

        for (DataStore entry : response.data()) {
            System.out.println(entry.getId() + " " + entry.getName());
        }
    }
}
```

Set `baseUrl` to the Barn API URL. If Barn requires authentication, see [Call the Wanaku services API](../guides/call-the-services-api.md).

## Next steps

- [Call the Wanaku services API](../guides/call-the-services-api.md): authenticate and use the other client operations.
- [Modules](../reference/modules.md): find the module for your task.
