---
title: Load service catalogs and routes
description: Use the Camel runtime module to clone a Git repository, fetch route files from Barn or disk, extract a service catalog, and load Camel routes.
---

# Load service catalogs and routes

The `capabilities-runtimes-camel-common` module gets Camel route files and their dependencies from Wanaku Barn, the local disk, or a Git repository. Then it loads the routes into a `CamelContext`. The Camel Integration Capability and the Wanaku Semantic Router use these classes at startup.

## Prerequisites

- The `capabilities-runtimes-camel-common` module. See [Get started](../get-started/index.md).
- A `ServicesHttpClient` for Barn, if you get resources from Barn. See [Call the Wanaku services API](call-the-services-api.md).
- A writable data directory. The classes on this page write files into it.

## Resource types

Each file that you get has a `ResourceType`:

| Type | Contents |
|---|---|
| `ROUTES_REF` | Camel route definitions, for example `routes.camel.yaml`. |
| `DEPENDENCY_REF` | Maven coordinates that the routes need at runtime. |
| `PROPERTIES_REF` | A properties file for the routes, usually `service.properties`. |

The classes return a `Map<ResourceType, Path>` with the local path of each file.

## Clone a Git repository

`InitializerFactory.createInitializer(initFrom, dataDir)` returns an `Initializer`:

- If `initFrom` is a Git URL (HTTPS or SSH), it returns a `GitInitializer`. `initialize()` clones the repository into `<dataDir>/cloned-repo`. If that directory is already a Git repository, `initialize()` uses it and does not clone again.
- If `initFrom` is null or blank, it returns a `NoOpInitializer`.

```java
Initializer initializer = InitializerFactory.createInitializer(initFrom, dataDir);
initializer.initialize();
```

## Get route files by URI

Use `ResourceListBuilder` to make the list of resources, and `DownloaderFactory` to get a `Downloader` for each URI scheme:

```java
List<ResourceRefs<URI>> refs = ResourceListBuilder.newBuilder()
        .addRoutesRef("datastore://routes.camel.yaml")
        .addDependenciesRef("datastore://dependencies.txt")
        .build();

DownloaderFactory factory = new DownloaderFactory(servicesHttpClient, dataDir);
Map<ResourceType, Path> downloaded = new HashMap<>();

for (ResourceRefs<URI> ref : refs) {
    factory.getDownloader(ref.ref()).downloadResource(ref, downloaded);
}
```

| Scheme | Downloader | Behavior |
|---|---|---|
| `datastore://<name>` | `DataStoreDownloader` | Gets the Barn data store entries with the name `<name>`, decodes the Base64 data, and writes it to `<dataDir>/<name>`. |
| `file:///<path>` | `FileDownloader` | Copies the file into `<dataDir>`. The file must exist. |

`build()` throws `IllegalStateException` if the list has no routes reference. Other schemes throw `IllegalArgumentException`.

To retry failed downloads, make a `RetryPolicy` with `ExponentialBackoffRetryPolicy.newBuilder()` (`maxRetries`, `initialDelayMillis`, `maxDelayMillis`) and use it in your download loop.

## Extract a service catalog

A service catalog is a ZIP archive that Barn stores. It contains an `index.properties` file and the files of one or more systems:

```properties
catalog.name=my-catalog
catalog.services=system1,system2
catalog.routes.system1=system1/routes.camel.yaml
catalog.dependencies.system1=system1/dependencies.txt
catalog.properties.system1=system1/service.properties
```

Get the catalog from Barn, then extract the files of one system:

```java
DataStore catalog = servicesHttpClient.getServiceCatalog("my-catalog").data();
Map<ResourceType, Path> files = ServiceCatalogExtractor.extract(catalog.getData(), "system1", dataDir);
```

`extract` writes all files of the archive into `dataDir` and returns the paths for the system:

- `ROUTES_REF` is always in the result. If `catalog.routes.<system>` is not set, `extract` throws a `WanakuException`.
- `DEPENDENCY_REF` is in the result if `catalog.dependencies.<system>` is set.
- `PROPERTIES_REF` is in the result if `catalog.properties.<system>` is set. If it is not set, `extract` uses a `service.properties` file in the directory of the routes file, if that file exists.

To make a catalog, see [Build and deploy a service catalog](https://wanaku.ai/docs/barn/next/manage/build-a-service-catalog.html).

## Load the routes

`WanakuRoutesLoader.loadRoute(context, path)` loads one route file into a `CamelContext`. The `path` is a `file:` URI.

```java
CamelContext context = new DefaultCamelContext();
new WanakuRoutesLoader().loadRoute(context, routesFile.toUri().toString());
context.start();
```

`loadRoute` does these steps:

1. If a `service.properties` file is in the directory of the route file, it adds the file as a Camel property source. It also sets each `forage.*` key in the file as a Java system property.
2. It loads each Camel `ContextServicePlugin` that `ServiceLoader` finds outside the SDK class loader.
3. It loads the routes from the file. If this fails, it throws a `RouteLoadingException`.
4. It builds the context. You start the context.

## Next steps

- [Download Maven artifacts](download-maven-artifacts.md): resolve the dependencies of the routes.
- [Modules](../reference/modules.md): the other runtime classes.
