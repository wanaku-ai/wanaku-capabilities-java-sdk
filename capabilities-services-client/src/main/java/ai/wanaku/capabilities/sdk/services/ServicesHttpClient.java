package ai.wanaku.capabilities.sdk.services;

import jakarta.ws.rs.core.MediaType;

import ai.wanaku.api.exceptions.WanakuException;
import ai.wanaku.api.types.DataStore;
import ai.wanaku.api.types.ForwardReference;
import ai.wanaku.api.types.Namespace;
import ai.wanaku.api.types.ResourceReference;
import ai.wanaku.api.types.ToolReference;
import ai.wanaku.api.types.WanakuResponse;
import ai.wanaku.api.types.io.ResourcePayload;
import ai.wanaku.api.types.io.ToolPayload;
import ai.wanaku.capabilities.sdk.common.exceptions.WanakuWebException;
import ai.wanaku.capabilities.sdk.common.serializer.Serializer;
import ai.wanaku.capabilities.sdk.services.config.ServicesClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
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

    /**
     * Constructs a {@code ServicesHttpClient} with the given configuration.
     *
     * @param config The {@link ServicesClientConfig} containing base URL and serializer.
     */
    public ServicesHttpClient(ServicesClientConfig config) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = sanitize(config);
        this.serializer = config.getSerializer();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Sanitizes the base URL from the configuration by removing a trailing slash if present.
     *
     * @param config The {@link ServicesClientConfig} to sanitize the base URL from.
     * @return The sanitized base URL.
     */
    private static String sanitize(ServicesClientConfig config) {
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
        return executePost("/api/v1/tools/add", toolReference, new TypeReference<WanakuResponse<ToolReference>>() {});
    }

    /**
     * Adds a new tool with payload (configuration and secrets).
     *
     * @param toolPayload The {@link ToolPayload} containing tool reference and configuration data.
     * @return The response containing the added tool reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ToolReference> addToolWithPayload(ToolPayload toolPayload) {
        return executePost("/api/v1/tools/addWithPayload", toolPayload, new TypeReference<WanakuResponse<ToolReference>>() {});
    }

    /**
     * Lists all available tools.
     *
     * @return The response containing the list of tool references.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<ToolReference>> listTools() {
        return executeGet("/api/v1/tools/list", new TypeReference<WanakuResponse<List<ToolReference>>>() {});
    }

    /**
     * Gets a tool by name.
     *
     * @param name The name of the tool to retrieve.
     * @return The response containing the tool reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ToolReference> getToolByName(String name) {
        return executePost("/api/v1/tools?name=" + name, "", new TypeReference<WanakuResponse<ToolReference>>() {});
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
        return executePost("/api/v1/resources/expose", resourceReference, new TypeReference<WanakuResponse<ResourceReference>>() {});
    }

    /**
     * Exposes a new resource with payload (configuration and secrets).
     *
     * @param resourcePayload The {@link ResourcePayload} containing resource reference and configuration data.
     * @return The response containing the exposed resource reference.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<ResourceReference> exposeResourceWithPayload(ResourcePayload resourcePayload) {
        return executePost("/api/v1/resources/exposeWithPayload", resourcePayload, new TypeReference<WanakuResponse<ResourceReference>>() {});
    }

    /**
     * Lists all available resources.
     *
     * @return The response containing the list of resource references.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<ResourceReference>> listResources() {
        return executeGet("/api/v1/resources/list", new TypeReference<WanakuResponse<List<ResourceReference>>>() {});
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
        return executeGet("/api/v1/forwards/list", new TypeReference<WanakuResponse<List<ForwardReference>>>() {});
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
        return executeGet("/api/v1/namespaces/list", new TypeReference<WanakuResponse<List<Namespace>>>() {});
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
        return executePost("/api/v1/data-store/add", dataStore, new TypeReference<WanakuResponse<DataStore>>() {});
    }

    /**
     * Lists all data stores.
     *
     * @return The response containing the list of all data stores.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<DataStore>> listDataStores() {
        return executeGet("/api/v1/data-store/list", new TypeReference<WanakuResponse<List<DataStore>>>() {});
    }

    /**
     * Gets a data store by ID.
     *
     * @param id The ID of the data store to retrieve.
     * @return The response containing the data store.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<DataStore> getDataStoreById(String id) {
        return executeGet("/api/v1/data-store/get?id=" + id, new TypeReference<WanakuResponse<DataStore>>() {});
    }

    /**
     * Gets data stores by name.
     *
     * @param name The name of the data stores to retrieve.
     * @return The response containing the list of data stores.
     * @throws WanakuException If an error occurs during the request.
     */
    public WanakuResponse<List<DataStore>> getDataStoresByName(String name) {
        return executeGet("/api/v1/data-store/get?name=" + name, new TypeReference<WanakuResponse<List<DataStore>>>() {});
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
}
