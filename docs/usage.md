# SDK Usage Guide

This guide provides instructions on how to use the Wanaku Capabilities Java SDK to generate new capabilities.

## Generating a New Capability Project

To create a new project based on the Wanaku Capabilities Java SDK archetype, use the following Maven command. Remember to replace `ai.test` with your desired package, `groupId`, `artifactId`, and `name`.

```bash
mvn -B archetype:generate -DarchetypeGroupId=ai.wanaku.sdk \
  -DarchetypeArtifactId=capabilities-archetypes-java-tool \
  -DarchetypeVersion=${WANAKU_VERSION} \
  -DgroupId=ai.test \
  -Dpackage=ai.test \
  -DartifactId=test \
  -Dname=Test \
  -Dwanaku-sdk-version=${WANAKU_VERSION}
```

**Explanation of Parameters:**

- `-DarchetypeGroupId`: The groupId of the archetype (always `ai.wanaku.sdk`).
- `-DarchetypeArtifactId`: The artifactId of the archetype (always `capabilities-archetypes-java-tool`).
- `-DarchetypeVersion`: The version of the archetype. Use the current version of the Wanaku SDK, typically passed as `${WANAKU_VERSION}`.
- `-DgroupId`: Your project's group ID (e.g., `com.mycompany`).
- `-Dpackage`: Your project's base package (e.g., `com.mycompany.mytool`).
- `-DartifactId`: Your project's artifact ID (e.g., `my-awesome-tool`).
- `-Dname`: A human-readable name for your capability (e.g., `My Awesome Tool`).
- `-Dwanaku-sdk-version`: The version of the Wanaku SDK to be used in the generated project. This should match `${WANAKU_VERSION}`.

## Learn More

- **[Client Registration Flow](client-registration-flow.md)** - Client Registration Flow
