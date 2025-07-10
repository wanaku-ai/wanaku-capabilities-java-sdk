package ai.wanaku.capabilities.sdk.discovery;

import jakarta.ws.rs.core.MediaType;

import ai.wanaku.api.types.discovery.ServiceState;
import ai.wanaku.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.discovery.config.DiscoveryServiceConfig;
import ai.wanaku.capabilities.sdk.discovery.serializer.Serializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class DiscoveryServiceHttpClient {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final Serializer serializer;

    private final String serviceBasePath = "/api/v1/management/discovery";

    public DiscoveryServiceHttpClient(DiscoveryServiceConfig config) {
        this.httpClient = HttpClient.newHttpClient();

        this.baseUrl = sanitize(config);
        this.serializer = config.getSerializer();
    }

    private static String sanitize(DiscoveryServiceConfig config) {
        // Ensure baseUrl doesn't have a trailing slash to prevent double slashes
        return config.getBaseUrl() != null && config.getBaseUrl().endsWith("/") ?
                config.getBaseUrl().substring(0, config.getBaseUrl().length() - 1) : config.getBaseUrl();
    }

    private HttpResponse<String> executePost(String operationPath, ServiceTarget serviceTarget) {
        try {
            String jsonRequestBody = serializer.serialize(serviceTarget);
            URI uri = URI.create(this.baseUrl + this.serviceBasePath + operationPath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Accept", MediaType.WILDCARD)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public HttpResponse<String> register(ServiceTarget serviceTarget) {
        return executePost("/register", serviceTarget);
    }

    public HttpResponse<String> deregister(ServiceTarget serviceTarget) {
        return executePost("/deregister", serviceTarget);
    }

    public HttpResponse<String> ping(String id) {
        try {
            String jsonRequestBody = serializer.serialize(id);
            URI uri = URI.create(this.baseUrl + this.serviceBasePath + "/ping/");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> updateState(String id, ServiceState serviceState) {
        try {
            String jsonRequestBody = serializer.serialize(serviceState);
            URI uri = URI.create(this.baseUrl + this.serviceBasePath + "/update/" + id);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Accept", MediaType.WILDCARD)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}