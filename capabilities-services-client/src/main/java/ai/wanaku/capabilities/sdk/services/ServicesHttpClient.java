package ai.wanaku.capabilities.sdk.services;

import jakarta.ws.rs.core.MediaType;

import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import ai.wanaku.capabilities.sdk.api.types.DataStore;
import ai.wanaku.capabilities.sdk.api.types.ForwardReference;
import ai.wanaku.capabilities.sdk.api.types.Namespace;
import ai.wanaku.capabilities.sdk.api.types.ResourceReference;
import ai.wanaku.capabilities.sdk.api.types.ToolReference;
import ai.wanaku.capabilities.sdk.api.types.WanakuResponse;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEvent;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionRequest;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionResponse;
import ai.wanaku.capabilities.sdk.api.types.io.ResourcePayload;
import ai.wanaku.capabilities.sdk.api.types.io.ToolPayload;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.common.exceptions.WanakuWebException;
import ai.wanaku.capabilities.sdk.common.serializer.Serializer;
import ai.wanaku.capabilities.sdk.security.ServiceAuthenticator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client for interacting with the Wanaku Services API.
 * This class handles HTTP requests for tools, resources, forwards, and namespaces operations.
 */
public class ServicesHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(ServicesHttpClient.class);

    private final HttpClient httpClient;
    private final String baseUrl;
    private final Serializer serializer;
    private final ObjectMapper objectMapper;
    private final ServiceAuthenticator serviceAuthenticator;

    /**
     * Constructs a {@code ServicesHttpClient} with the given configuration.
     *
     * @param config The {@link ServiceConfig} containing base URL and serializer.
     */
    public ServicesHttpClient(ServiceConfig config) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = sanitize(config);
        this.serializer = config.getSerializer();
        this.objectMapper = new ObjectMapper();
        this.serviceAuthenticator = new ServiceAuthenticator(config);
    }

    /**
     * Sanitizes the base URL from the configuration by removing a trailing slash if present.
     *
     * @param config The {@link ServiceConfig} to sanitize the base URL from.
     * @return The sanitized base URL.
     */
    private static String sanitize(ServiceConfig config) {
        return config.getBaseUrl() != null && config.getBaseUrl().endsWith("/") ?
                config.getBaseUrl().substring(0, config.getBaseUrl().length() - 1) : config.getBaseUrl();
    }

    /**
     * Executes a POST request to the Services API.
     *
     * @param path The API endpoint path.
     * @param payload The payload object to be sent in the request body.
     * @param typeReference The type reference for deserializing the response.
     * @param <T> The type of the response.
     * @param <R> The type of the payload.
     * @return The deserialized response object.
     * @throws WanakuException If an error occurs during the request.
     */
    private <T, R> T executePost(String path, R payload, TypeReference<T> typeReference) {
        try {
            String jsonRequestBody = serializer.serialize(payload);
            URI uri = URI.create(this.baseUrl + path);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Accept", MediaType.APPLICATION_JSON)
                    .header("Authorization", serviceAuthenticator.toHeaderValue())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), typeReference);
            } else {
                throw new WanakuWebException("HTTP error: " + response.statusCode() + " - " + response.body(), response.statusCode());
            }
        } catch (JsonProcessingException e) {
            throw new WanakuException("JSON processing error", e);
        } catch (IOException e) {
            throw new WanakuException("I/O error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WanakuException("Request interrupted", e);
        }
    }

    /**
     * Executes a PUT request to the Services API.
     *
     * @param path The API endpoint path.
     * @param payload The payload object to be sent in the request body.
     * @param <R> The type of the payload.
     * @throws WanakuException If an error occurs during the request.
     */
    private <R> void executePut(String path, R payload) {
        try {
            String jsonRequestBody = serializer.serialize(payload);
            URI uri = URI.create(this.baseUrl + path);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Accept", MediaType.APPLICATION_JSON)
                    .header("Authorization", serviceAuthenticator.toHeaderValue())
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new WanakuWebException("HTTP error: " + response.statusCode() + " - " + response.body(), response.statusCode());
            }
        } catch (JsonProcessingException e) {
            throw new WanakuException("JSON processing error", e);
        } catch (IOException e) {
            throw new WanakuException("I/O error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WanakuException("Request interrupted", e);
        }
    }

    /**
     * Executes a GET request to the Services API.
     *
     * @param path The API endpoint path.
     * @param typeReference The type reference for deserializing the response.
     * @param <T> The type of the response.
     * @return The deserialized response object.
     * @throws WanakuException If an error occurs during the request.
     */
    private <T> T executeGet(String path, TypeReference<T> typeReference) {
        try {
            URI uri = URI.create(this.baseUrl + path);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", MediaType.APPLICATION_JSON)
                    .header("Authorization", serviceAuthenticator.toHeaderValue())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), typeReference);
            } else {
                throw new WanakuWebException("HTTP error: " + response.statusCode() + " - " + response.body(), response.statusCode());
            }
        } catch (JsonProcessingException e) {
            throw new WanakuException("JSON processing error", e);
        } catch (IOException e) {
            throw new WanakuException("I/O error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WanakuException("Request interrupted", e);
        }
    }

    /**
     * Executes a DELETE request (using PUT with query param) to the Services API.
     *
     * @param path The API endpoint path with query parameters.
     * @throws WanakuException If an error occurs during the request.
     */
    private void executeDelete(String path) {
        try {
            URI uri = URI.create(this.baseUrl + path);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", MediaType.APPLICATION_JSON)
                    .header("Authorization", serviceAuthenticator.toHeaderValue())
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new WanakuException("HTTP error: " + response.statusCode() + " - " + response.body());
            }
        } catch (IOException e) {
            throw new WanakuException("I/O error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WanakuException("Request interrupted", e);
        }
    }

    // ==================== Tools API Methods ====================

    /**
     * Adds a new tool.
     *
     * @param toolReference The {@link ToolReference} to add.
     * @return The response containing the added tool reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ToolReference> addTool(ToolReference toolReference) {
        return executePost("/api/v1/tools/add", toolReference, new TypeReference<>() {
        });
    }

    /**
     * Adds a new tool with payload (configuration and secrets).
     *
     * @param toolPayload The {@link ToolPayload} containing tool reference and configuration data.
     * @return The response containing the added tool reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ToolReference> addToolWithPayload(ToolPayload toolPayload) {
        return executePost("/api/v1/tools/addWithPayload", toolPayload, new TypeReference<>() {
        });
    }

    /**
     * Lists all available tools.
     *
     * @return The response containing the list of tool references.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<ToolReference>> listTools() {
        return executeGet("/api/v1/tools/list", new TypeReference<>() {
        });
    }

    /**
     * Gets a tool by name.
     *
     * @param name The name of the tool to retrieve.
     * @return The response containing the tool reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ToolReference> getToolByName(String name) {
        return executePost("/api/v1/tools?name=" + name, "", new TypeReference<>() {
        });
    }

    /**
     * Updates an existing tool.
     *
     * @param toolReference The {@link ToolReference} with updated information.
     * @throws WanakuException If an error occurs during the request.
     */
    public void updateTool(ToolReference toolReference) {
        executePost("/api/v1/tools/update", toolReference, new TypeReference<WanakuResponse<Void>>() {});
    }

    /**
     * Removes a tool by name.
     *
     * @param toolName The name of the tool to remove.
     * @throws WanakuException If an error occurs during the request.
     */
    public void removeTool(String toolName) {
        executeDelete("/api/v1/tools/remove?tool=" + toolName);
    }

    // ==================== Resources API Methods ====================

    /**
     * Exposes a new resource.
     *
     * @param resourceReference The {@link ResourceReference} to expose.
     * @return The response containing the exposed resource reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ResourceReference> exposeResource(ResourceReference resourceReference) {
        return executePost("/api/v1/resources/expose", resourceReference, new TypeReference<>() {
        });
    }

    /**
     * Exposes a new resource with payload (configuration and secrets).
     *
     * @param resourcePayload The {@link ResourcePayload} containing resource reference and configuration data.
     * @return The response containing the exposed resource reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ResourceReference> exposeResourceWithPayload(ResourcePayload resourcePayload) {
        return executePost("/api/v1/resources/exposeWithPayload", resourcePayload, new TypeReference<>() {
        });
    }

    /**
     * Lists all available resources.
     *
     * @return The response containing the list of resource references.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<ResourceReference>> listResources() {
        return executeGet("/api/v1/resources/list", new TypeReference<>() {
        });
    }

    /**
     * Updates an existing resource.
     *
     * @param resourceReference The {@link ResourceReference} with updated information.
     * @throws WanakuException If an error occurs during the request.
     */
    public void updateResource(ResourceReference resourceReference) {
        executePost("/api/v1/resources/update", resourceReference, new TypeReference<WanakuResponse<Void>>() {});
    }

    /**
     * Removes a resource by name.
     *
     * @param resourceName The name of the resource to remove.
     * @throws WanakuException If an error occurs during the request.
     */
    public void removeResource(String resourceName) {
        executeDelete("/api/v1/resources/remove?resource=" + resourceName);
    }

    // ==================== Forwards API Methods ====================

    /**
     * Adds a new forward reference.
     *
     * @param forwardReference The {@link ForwardReference} to add.
     * @throws WanakuException If an error occurs during the request.
     */
    public void addForward(ForwardReference forwardReference) {
        executePost("/api/v1/forwards/add", forwardReference, new TypeReference<WanakuResponse<Void>>() {});
    }

    /**
     * Lists all forward references.
     *
     * @return The response containing the list of forward references.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<ForwardReference>> listForwards() {
        return executeGet("/api/v1/forwards/list", new TypeReference<>() {
        });
    }

    /**
     * Updates an existing forward reference.
     *
     * @param forwardReference The {@link ForwardReference} with updated information.
     * @throws WanakuException If an error occurs during the request.
     */
    public void updateForward(ForwardReference forwardReference) {
        executePost("/api/v1/forwards/update", forwardReference, new TypeReference<WanakuResponse<Void>>() {});
    }

    /**
     * Removes a forward reference.
     *
     * @param forwardReference The {@link ForwardReference} to remove.
     * @throws WanakuException If an error occurs during the request.
     */
    public void removeForward(ForwardReference forwardReference) {
        executePut("/api/v1/forwards/remove", forwardReference);
    }

    // ==================== Namespaces API Methods ====================

    /**
     * Lists all namespaces.
     *
     * @return The response containing the list of namespaces.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<Namespace>> listNamespaces() {
        return executeGet("/api/v1/namespaces/list", new TypeReference<>() {
        });
    }

    // ==================== DataStores API Methods ====================

    /**
     * Adds a new data store entry.
     *
     * @param dataStore The {@link DataStore} to add.
     * @return The response containing the added data store.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<DataStore> addDataStore(DataStore dataStore) {
        return executePost("/api/v1/data-store/add", dataStore, new TypeReference<>() {
        });
    }

    /**
     * Lists all data stores.
     *
     * @return The response containing the list of all data stores.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<DataStore>> listDataStores() {
        return executeGet("/api/v1/data-store/list", new TypeReference<>() {
        });
    }

    /**
     * Gets a data store by ID.
     *
     * @param id The ID of the data store to retrieve.
     * @return The response containing the data store.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<DataStore> getDataStoreById(String id) {
        return executeGet("/api/v1/data-store/get?id=" + id, new TypeReference<>() {
        });
    }

    /**
     * Gets data stores by name.
     *
     * @param name The name of the data stores to retrieve.
     * @return The response containing the list of data stores.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<DataStore>> getDataStoresByName(String name) {
        return executeGet("/api/v1/data-store/get?name=" + name, new TypeReference<>() {
        });
    }

    /**
     * Removes a data store by ID.
     *
     * @param id The ID of the data store to remove.
     * @throws WanakuException If an error occurs during the request.
     */
    public void removeDataStore(String id) {
        executeDelete("/api/v1/data-store/remove?id=" + id);
    }

    /**
     * Removes data stores by name.
     *
     * @param name The name of the data stores to remove.
     * @throws WanakuException If an error occurs during the request.
     */
    public void removeDataStoresByName(String name) {
        executeDelete("/api/v1/data-store/remove?name=" + name);
    }

    // ==================== Code Execution Engine API Methods ====================

    /**
     * Submits code for execution to the Wanaku Code Execution Engine.
     * <p>
     * This method submits a code execution request to the specified engine type and language.
     * The response contains a task ID and stream URL that can be used to monitor the execution
     * progress via Server-Sent Events (SSE).
     * <p>
     * Example usage:
     * <pre>{@code
     * CodeExecutionRequest request = new CodeExecutionRequest("System.out.println(\"Hello\");");
     * WanakuResponse<CodeExecutionResponse> response = client.executeCode("jvm", "java", request);
     * String taskId = response.getData().taskId();
     * String streamUrl = response.getData().streamUrl();
     * }</pre>
     *
     * @param engineType The type of execution engine (e.g., "jvm", "interpreted").
     * @param language The programming language (e.g., "java", "groovy", "xml").
     * @param request The code execution request containing the code and execution parameters.
     * @return The response containing the task ID and stream URL wrapped in WanakuResponse.
     * @throws WanakuException If an error occurs during the request.
     * @since 1.0.0
     */
    public WanakuResponse<CodeExecutionResponse> executeCode(
            String engineType, String language, CodeExecutionRequest request) {
        // Validate the request before sending
        request.validate();

        String path = String.format("/api/v2/code-execution-engine/%s/%s", engineType, language);
        return executePost(path, request, new TypeReference<>() {});
    }

    /**
     * Streams code execution events from the SSE endpoint.
     * <p>
     * This method connects to the Server-Sent Events (SSE) stream endpoint for a specific
     * code execution task and consumes events as they arrive. The consumer callback is
     * invoked for each event received from the stream.
     * <p>
     * The stream will continue until the execution completes (COMPLETED, FAILED, TIMEOUT,
     * or CANCELLED event) or an error occurs.
     * <p>
     * Example usage:
     * <pre>{@code
     * client.streamCodeExecutionEvents("jvm", "java", taskId, event -> {
     *     switch (event.getEventType()) {
     *         case STARTED -> System.out.println("Execution started");
     *         case OUTPUT -> System.out.print(event.getOutput());
     *         case ERROR -> System.err.print(event.getError());
     *         case COMPLETED -> System.out.println("Exit code: " + event.getExitCode());
     *         case FAILED -> System.err.println("Execution failed: " + event.getMessage());
     *     }
     * });
     * }</pre>
     *
     * @param engineType The type of execution engine (e.g., "jvm", "interpreted").
     * @param language The programming language (e.g., "java", "groovy", "xml").
     * @param taskId The UUID of the execution task.
     * @param eventConsumer The consumer callback to handle each event.
     * @throws WanakuException If an error occurs during streaming.
     * @since 1.0.0
     */
    public void streamCodeExecutionEvents(
            String engineType, String language, String taskId, Consumer<CodeExecutionEvent> eventConsumer) {
        String path = String.format("/api/v2/code-execution-engine/%s/%s/%s", engineType, language, taskId);
        URI uri = URI.create(this.baseUrl + path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "text/event-stream")
                .header("Authorization", serviceAuthenticator.toHeaderValue())
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                parseSSEStream(response.body(), eventConsumer);
            } else {
                throw new WanakuWebException(
                        "Failed to connect to SSE stream: HTTP " + response.statusCode(),
                        response.statusCode());
            }
        } catch (IOException e) {
            throw new WanakuException("I/O error while streaming events", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WanakuException("Event streaming interrupted", e);
        }
    }

    /**
     * Parses an SSE stream and invokes the consumer for each event.
     *
     * @param inputStream The input stream containing SSE data.
     * @param eventConsumer The consumer callback to handle each event.
     * @throws IOException If an I/O error occurs while reading the stream.
     */
    private void parseSSEStream(InputStream inputStream, Consumer<CodeExecutionEvent> eventConsumer)
            throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            StringBuilder dataBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // SSE format: lines starting with "data:" contain the JSON payload
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim(); // Remove "data:" prefix
                    dataBuilder.append(data);
                } else if (line.isEmpty() && dataBuilder.length() > 0) {
                    // Empty line signals end of an event
                    String jsonData = dataBuilder.toString();
                    dataBuilder.setLength(0); // Clear for next event

                    try {
                        CodeExecutionEvent event = objectMapper.readValue(jsonData, CodeExecutionEvent.class);
                        eventConsumer.accept(event);

                        // Stop streaming if we received a terminal event
                        if (isTerminalEvent(event)) {
                            LOG.debug("Received terminal event: {}", event.getEventType());
                            break;
                        }
                    } catch (JsonProcessingException e) {
                        LOG.warn("Failed to parse SSE event data: {}", jsonData, e);
                        // Continue processing other events even if one fails to parse
                    }
                }
            }
        }
    }

    /**
     * Checks if an event is a terminal event (indicates end of execution).
     *
     * @param event The code execution event to check.
     * @return true if the event is terminal, false otherwise.
     */
    private boolean isTerminalEvent(CodeExecutionEvent event) {
        if (event.getEventType() == null) {
            return false;
        }
        return switch (event.getEventType()) {
            case COMPLETED, FAILED, TIMEOUT, CANCELLED -> true;
            default -> false;
        };
    }
}
