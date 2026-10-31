---
title: Download Maven artifacts
description: Use WanakuMavenDownloader to resolve Maven artifacts and their transitive dependencies at runtime and load them through a class loader.
---

# Download Maven artifacts

`WanakuMavenDownloader` resolves Maven artifacts and their transitive dependencies at runtime, and makes them available through a class loader. Use it to add libraries that a route or a plugin needs, without a rebuild.

## Prerequisites

- The `capabilities-maven-downloader` module. See [Get started](../get-started/index.md).
- Network access to Maven Central or to your repositories.

## Download artifacts

```java
try (WanakuMavenDownloader downloader = new WanakuMavenDownloader()) {
    List<Path> jars = downloader.download(List.of(
            GAV.parse("org.apache.camel:camel-jackson:4.22.0")));
    downloader.applyToCurrentThread();
    // Classes from camel-jackson and its dependencies are now loadable.
}
```

`download` resolves all given artifacts as one graph, with Maven conflict mediation. It returns the paths of all resolved JAR files, transitive dependencies included. If an artifact cannot be resolved, it throws a `DependencyDownloadException`.

After the download:

- `getClassLoader()` returns a class loader with all downloaded artifacts.
- `applyToCurrentThread()` sets that class loader as the context class loader of the current thread.
- `close()` releases the class loader. Use try-with-resources, or call `close()` when you no longer need the classes.

## Select repositories and the cache

| Constructor | Remote repositories | Local repository |
|---|---|---|
| `WanakuMavenDownloader()` | Maven Central | `~/.m2/repository` |
| `WanakuMavenDownloader(List<Repository>)` | Maven Central and the given repositories | `~/.m2/repository` |
| `WanakuMavenDownloader(List<Repository>, Path)` | Maven Central and the given repositories | The given path |
| `WanakuMavenDownloader(List<Repository>, Path, ClassLoader)` | Maven Central and the given repositories | The given path |

Use the constructor with a `ClassLoader` argument to set the parent of the new class loader. `WanakuMavenDownloader(ClassLoader)` does the same with the default repositories and cache.

A `Repository` has an `id` and a `url`:

```java
new Repository("company", "https://repo.example.com/maven2")
```

## Write coordinates

`GAV.parse("groupId:artifactId:version")` reads a coordinate. It throws `IllegalArgumentException` if the text does not have three parts.

`GAV.parse("groupId:artifactId", properties)` gets the version from a `Properties` object. It looks for the key `groupId:artifactId` first, then for the key `groupId`.

## Next steps

- [Load service catalogs and routes](load-catalogs-and-routes.md): get the dependency files of a route.
- [Modules](../reference/modules.md): the other SDK modules.
