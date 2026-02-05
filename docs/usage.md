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

## Integrating Existing Camel Applications

For applications already using Apache Camel (4.14.0+), you can expose existing routes as MCP tools using the Camel Integration Capability plugin. This approach requires no code changes to your routes.

### Adding the Plugin Dependency

Add the following dependency to your project:

```xml
<dependency>
    <groupId>ai.wanaku.sdk</groupId>
    <artifactId>capabilities-runtime-camel-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

The plugin is automatically discovered via Camel's SPI mechanism and activates during context startup.

### Configuration

Configuration is provided via environment variables (recommended) or a properties file. Environment variables take precedence.

#### Required Configuration

| Environment Variable | Description                                                                    |
|----------------------|--------------------------------------------------------------------------------|
| `REGISTRATION_URL`   | URL of the Wanaku router (e.g., `http://localhost:8080`)                       |
| `CLIENT_ID`          | OAuth2 client identifier for authentication                                    |
| `CLIENT_SECRET`      | OAuth2 client secret                                                           |
| `ROUTES_RULES`       | Path to the rules file defining MCP tools (e.g., `file:///path/to/rules.yaml`) |

#### Optional Configuration

| Environment Variable            | Default | Description                                |
|---------------------------------|---------|--------------------------------------------|
| `GRPC_PORT`                     | `9190`  | Port for the gRPC server                   |
| `SERVICE_NAME`                  | `camel` | Service name for registration              |
| `REGISTRATION_ANNOUNCE_ADDRESS` | `auto`  | Address announced to the router            |
| `ROUTES_PATH`                   | -       | Reference to additional routes file/URL    |
| `DATA_DIR`                      | `/tmp`  | Directory for storing downloaded resources |
| `INIT_FROM`                     | -       | Git repository URL to clone at startup     |

#### Retry Configuration

| Environment Variable | Default | Description                                       |
|----------------------|---------|---------------------------------------------------|
| `RETRIES`            | `12`    | Number of registration retry attempts             |
| `RETRY_WAIT_SECONDS` | `5`     | Wait time between retries (seconds)               |
| `INITIAL_DELAY`      | `5`     | Delay before first registration attempt (seconds) |
| `PERIOD`             | `5`     | Registration ping period (seconds)                |

#### Properties File Example

As an alternative to environment variables, create a `camel-integration-capability.properties` file in your classpath:

```properties
# Registration (required)
registration.url=http://localhost:8080
registration.announce.address=localhost

# gRPC Configuration
grpc.port=9190

# Service Identity
service.name=my-camel-service

# Routes Configuration
rules.ref=file:///path/to/rules.yaml

# Authentication (required)
client.id=wanaku-service
client.secret=your-secret-here

# Optional: Additional routes
routes.ref=

# Optional: Data directory
data.dir=/tmp

# Optional: Retry settings
retries=12
retry.wait.seconds=5
initial.delay=5
period=5
```

Environment variables override properties file values when both are present.

### Defining MCP Tools

Create a YAML rules file to define which routes to expose as MCP tools:

```yaml
tools:
  - name: my-tool-name
    description: "Description of what this tool does"
    route: direct:my-route
    inputSchema:
      type: object
      properties:
        param1:
          type: string
          description: "Parameter description"
      required:
        - param1
```

The `ROUTES_RULES` variable supports:
- File paths: `file:///path/to/rules.yaml`
- Datastore references: `datastore://rules.yaml`

### Running the Application

```bash
REGISTRATION_URL=http://localhost:8080 \
REGISTRATION_ANNOUNCE_ADDRESS=localhost \
GRPC_PORT=9190 \
SERVICE_NAME=my-camel-service \
ROUTES_RULES=file://$PWD/config/rules.yaml \
CLIENT_ID=wanaku-service \
CLIENT_SECRET=<your-secret> \
java -jar target/my-app.jar
```

Once running, the plugin:
1. Registers with the Wanaku router
2. Loads the MCP tool definitions from the rules file
3. Starts a gRPC server exposing your routes as callable tools

### Example

For a complete working example, see the [Cat Facts Example](https://github.com/wanaku-ai/wanaku-demos/tree/main/06-camel-integration-capability-existing-route/sample-routes/camel-core-examples/cat-facts-example) in the Wanaku Demos repository.
=======
## Accessing Request Data

The `ToolInvokeRequest` provides several methods to access invocation data:

| Method | Description |
|--------|-------------|
| `getArgumentsMap()` | Tool arguments passed by the caller |
| `getHeadersMap()` | HTTP headers including metadata headers |
| `getBody()` | Request body content (from `wanaku_body` argument) |
| `getUri()` | The tool's configured URI |
| `getConfigurationURI()` | URI for external configuration |
| `getSecretsURI()` | URI for secrets/credentials |

## Accessing Metadata Headers

AI services can inject metadata into tool invocations using the `wanaku_meta_*` prefix convention. Arguments prefixed with `wanaku_meta_` are automatically:

1. Extracted from regular arguments
2. Prefix stripped to form the header name
3. Made available via `request.getHeadersMap()`

### Example: LangChain4j AI Service

```java
@RegisterAiService
public interface MyAIService {
    @McpToolBox("wanakutoolbox")
    String callTool(
        @Header("wanaku_meta_contextId") String contextId,
        @Header("wanaku_meta_userId") String userId,
        @UserMessage String message
    );
}
```

### Accessing Headers in Tool Implementation

```java
public void invokeTool(ToolInvokeRequest request, StreamObserver<ToolInvokeReply> responseObserver) {
    // Access metadata headers (prefix is stripped)
    Map<String, String> headers = request.getHeadersMap();
    String contextId = headers.get("contextId");  // from wanaku_meta_contextId
    String userId = headers.get("userId");        // from wanaku_meta_userId

    // Use headers for context-aware processing
    if (contextId != null) {
        // Process with context...
    }

    // Regular arguments (metadata args are filtered out)
    Map<String, String> args = request.getArgumentsMap();
    // ...
}
```

### Reserved Argument Names

The SDK provides constants for reserved argument names:

```java
import ai.wanaku.capabilities.sdk.api.util.ReservedArgumentNames;

// Body content argument
String bodyArg = ReservedArgumentNames.BODY;  // "wanaku_body"

// Metadata prefix for header injection
String prefix = ReservedArgumentNames.METADATA_PREFIX;  // "wanaku_meta_"
```

| Constant | Value | Purpose |
|----------|-------|---------|
| `BODY` | `wanaku_body` | Argument containing request body content |
| `METADATA_PREFIX` | `wanaku_meta_` | Prefix for arguments converted to headers |


# Learn More

- **[Client Registration Flow](client-registration-flow.md)** - Client Registration Flow
