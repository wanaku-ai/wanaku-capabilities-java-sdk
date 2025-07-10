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

*   `-DarchetypeGroupId`: The groupId of the archetype (always `ai.wanaku.sdk`).
*   `-DarchetypeArtifactId`: The artifactId of the archetype (always `capabilities-archetypes-java-tool`).
*   `-DarchetypeVersion`: The version of the archetype. Use the current version of the Wanaku SDK, typically passed as `${WANAKU_VERSION}`.
*   `-DgroupId`: Your project's group ID (e.g., `com.mycompany`).
*   `-Dpackage`: Your project's base package (e.g., `com.mycompany.mytool`).
*   `-DartifactId`: Your project's artifact ID (e.g., `my-awesome-tool`).
*   `-Dname`: A human-readable name for your capability (e.g., `My Awesome Tool`).
*   `-Dwanaku-sdk-version`: The version of the Wanaku SDK to be used in the generated project. This should match `${WANAKU_VERSION}`.

## Modifying the Generated Tool Class

After generating the project, navigate into the newly created project directory. You will find a class named `AppTool` in your main source folder (`src/main/java/...`).

Modify the `toolInvoke` method within this `AppTool` class to implement the actual logic of your capability. This method is the entry point for executing your tool's functionality when invoked by the Wanaku platform.

```java
package ai.test;

import ai.wanaku.core.exchange.ToolInvokeReply;
import ai.wanaku.core.exchange.ToolInvokeRequest;
import ai.wanaku.core.exchange.ToolInvokerGrpc;
import io.grpc.stub.StreamObserver;
import java.util.List;

public class AppTool extends ToolInvokerGrpc.ToolInvokerImplBase {

    public void invokeTool(ToolInvokeRequest request, StreamObserver<ToolInvokeReply> responseObserver) {

        try {
            // Here, write the code to actually invoke the tool, then set whatever is returned as the
            // response object (i.e; Object response = myToolCall();)
            Object response = null;

            // Build the response
            responseObserver.onNext(
                    ToolInvokeReply.newBuilder()
                            .setIsError(false)
                            .addAllContent(List.of(response.toString())).build());

            responseObserver.onCompleted();
        } finally {
            // cleanup
        }
    }
}
```

Replace the `// TODO: Implement your tool's logic here` comment with your specific business logic. 

The `ToolInvokeRequest` object provides access to various information and services relevant to the tool's execution request.
