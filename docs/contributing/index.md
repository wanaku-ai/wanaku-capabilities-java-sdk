---
title: Contributing
description: Build and test the Wanaku Capabilities Java SDK, run static analysis and coverage, write documentation, and release a new SDK version.
---

# Contributing

This section is for developers who change the Wanaku Capabilities Java SDK.

| Page | Contents |
|---|---|
| [Write SDK documentation](documentation.md) | Page conventions and the docs checks that CI runs. |
| [Release guide](release.md) | Cut a release from an `X.Y.x` branch. |

## Build and test

The SDK uses Maven and Java 21. The project has no Maven wrapper.

```bash
mvn clean install                 # build all modules
mvn test                          # run the unit tests
mvn verify -Pstatic-analysis      # PMD and SpotBugs
mvn verify -Pcoverage             # JaCoCo aggregate report
```

Spotless formats the code with Palantir Java Format in the `compile` phase. Wildcard imports are not permitted. Run the static analysis profile before you open a pull request.

## Next steps

- [Write SDK documentation](documentation.md): follow the page conventions.
- [Release guide](release.md): release a new version.
