package ai.wanaku.capabilities.sdk.discovery;

import jakarta.ws.rs.core.MediaType;

import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import ai.wanaku.capabilities.sdk.api.types.discovery.ServiceState;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.discovery.exceptions.InvalidResponseDataException;
import ai.wanaku.capabilities.sdk.common.serializer.Serializer;
import ai.wanaku.capabilities.sdk.security.ServiceAuthenticator;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client for interacting with the Wanaku Discovery and Registration API.
 * This class handles HTTP requests for service registration, deregistration, ping, and state updates.
 */
public class DiscoveryServiceHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryServiceHttpClient.class);

    private final HttpClient httpClient;
    private final String baseUrl;
    private final Serializer serializer;

    private final String serviceBasePath = "/api/v1/management/discovery";

    private final ServiceAuthenticator serviceAuthenticator;

    /**
     * Constructs a {@code DiscoveryServiceHttpClient} with the given configuration.
     *
     * @param config The {@link ServiceConfig} containing base URL and serializer.
     */
    public DiscoveryServiceHttpClient(ServiceConfig config) {
        this.httpClient = HttpClient.newHttpClient();

        this.baseUrl = sanitize(config);
        this.serializer = config.getSerializer();

        serviceAuthenticator = new ServiceAuthenticator(config);
    }

    /**
     * Sanitizes the base URL from the configuration by removing a trailing slash if present.
     *
     * @param config The {@link ServiceConfig} to sanitize the base URL from.
     * @return The sanitized base URL.
     */
    private static String sanitize(ServiceConfig config) {
        // Ensure baseUrl doesn't have a trailing slash to prevent double slashes
        return config.getBaseUrl() != null && config.getBaseUrl().endsWith("/") ?
                config.getBaseUrl().substring(0, config.getBaseUrl().length() - 1) : config.getBaseUrl();
    }

    /**
     * Executes a POST request to the Discovery API.
     *
     * @param operationPath The specific API endpoint path (e.g., "/register").
     * @param serviceTarget The {@link ServiceTarget} object to be sent in the request body.
     * @return The {@link HttpResponse} from the API.
     * @throws RuntimeException If JSON processing fails or an I/O error occurs during the request.
     */
    private HttpResponse<String> executePost(String operationPath, ServiceTarget serviceTarget) {

        try {
            String jsonRequestBody = serializer.serialize(serviceTarget);
            URI uri = URI.create(this.baseUrl + this.serviceBasePath + operationPath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Accept", MediaType.WILDCARD)
                    .header("Authorization", serviceAuthenticator.toHeaderValue())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (JsonProcessingException e) {
            throw new InvalidResponseDataException(e);
        } catch (IOException e) {
            throw new WanakuException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Interrupted while waiting for request to POST");
            return null;
        }
    }

    /**
     * Registers a service with the Wanaku Discovery API.
     *
     * @param serviceTarget The {@link ServiceTarget} representing the service to register.
     * @return The {@link HttpResponse} from the registration API call.
     */
    public HttpResponse<String> register(ServiceTarget serviceTarget) {
        return executePost("/register", serviceTarget);
    }

    /**
     * Deregisters a service from the Wanaku Discovery API.
     *
     * @param serviceTarget The {@link ServiceTarget} representing the service to deregister.
     * @return The {@link HttpResponse} from the deregistration API call.
     */
    public HttpResponse<String> deregister(ServiceTarget serviceTarget) {
        return executePost("/deregister", serviceTarget);
    }

    /**
     * Sends a ping request to the Wanaku Discovery API for a given service ID.
     *
     * @param id The ID of the service to ping.
     * @return The {@link HttpResponse} from the ping API call.
     * @throws RuntimeException If an I/O error occurs during the request.
     */
    public HttpResponse<String> ping(String id) {
        try {
            URI uri = URI.create(this.baseUrl + this.serviceBasePath + "/ping/");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", serviceAuthenticator.toHeaderValue())
                    .POST(HttpRequest.BodyPublishers.ofString(id))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the state of a service with the Wanaku Discovery API.
     *
     * @param id The ID of the service whose state is to be updated.
     * @param serviceState The new {@link ServiceState} of the service.
     * @return The {@link HttpResponse} from the update state API call.
     * @throws RuntimeException If JSON processing fails or an I/O error occurs during the request.
     */
    public HttpResponse<String> updateState(String id, ServiceState serviceState) {
        try {
            String jsonRequestBody = serializer.serialize(serviceState);
            URI uri = URI.create(this.baseUrl + this.serviceBasePath + "/update/" + id);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Accept", MediaType.WILDCARD)
                    .header("Authorization", serviceAuthenticator.toHeaderValue())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (JsonProcessingException e) {
            throw new InvalidResponseDataException(e);
        } catch (InterruptedException | IOException e) {
            throw new WanakuException(e);
        }
    }
}