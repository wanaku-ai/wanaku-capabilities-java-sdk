package ai.wanaku.capabilities.sdk.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ai.wanaku.capabilities.sdk.api.types.DataStore;
import ai.wanaku.capabilities.sdk.api.types.ForwardReference;
import ai.wanaku.capabilities.sdk.api.types.ResourceReference;
import ai.wanaku.capabilities.sdk.api.types.ToolReference;
import ai.wanaku.capabilities.sdk.common.config.DefaultServiceConfig;
import ai.wanaku.capabilities.sdk.common.serializer.JacksonSerializer;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServicesHttpClientTest {

    private HttpServer server;
    private ServicesHttpClient client;
    private final List<RequestRecord> requests = new CopyOnWriteArrayList<>();

    record RequestRecord(String method, String path, String body) {}

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        int port = server.getAddress().getPort();
        String baseUrl = "http://localhost:" + port;

        // OIDC discovery endpoint
        server.createContext("/.well-known/openid-configuration", exchange -> {
            String oidcConfig = String.format(
                    "{\"issuer\":\"%s\",\"token_endpoint\":\"%s/token\","
                            + "\"authorization_endpoint\":\"%s/auth\","
                            + "\"jwks_uri\":\"%s/jwks\","
                            + "\"subject_types_supported\":[\"public\"]}",
                    baseUrl, baseUrl, baseUrl, baseUrl);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, oidcConfig.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(oidcConfig.getBytes());
            }
        });

        // Token endpoint
        server.createContext("/token", exchange -> {
            String tokenResponse = "{\"access_token\":\"test-token\",\"token_type\":\"Bearer\",\"expires_in\":3600}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, tokenResponse.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(tokenResponse.getBytes());
            }
        });

        // Catch-all API handler
        server.createContext("/api/", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes());
            String fullPath = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                fullPath = fullPath + "?" + query;
            }
            requests.add(new RequestRecord(exchange.getRequestMethod(), fullPath, body));

            String response = "{\"data\":null}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.start();

        DefaultServiceConfig config = DefaultServiceConfig.Builder.newBuilder()
                .baseUrl(baseUrl)
                .serializer(new JacksonSerializer())
                .clientId("test-client")
                .secret("test-secret")
                .tokenEndpoint(baseUrl)
                .build();

        client = new ServicesHttpClient(config);
    }

    @AfterEach
    void tearDown() {
        requests.clear();
        server.stop(0);
    }

    // ==================== Tools API Tests ====================

    @Test
    void addToolUsesPostAtToolsRoot() {
        ToolReference tool = new ToolReference();
        tool.setName("test-tool");

        client.addTool(tool);

        assertEquals(1, requests.size());
        assertEquals("POST", requests.getFirst().method());
        assertEquals("/api/v1/tools", requests.getFirst().path());
    }

    @Test
    void listToolsUsesGetAtToolsRoot() {
        client.listTools();

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/tools", requests.getFirst().path());
    }

    @Test
    void getToolByNameUsesGetWithPathParam() {
        client.getToolByName("my-tool");

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/tools/my-tool", requests.getFirst().path());
    }

    @Test
    void updateToolUsesPutWithPathParam() {
        ToolReference tool = new ToolReference();
        tool.setName("my-tool");

        client.updateTool("my-tool", tool);

        assertEquals(1, requests.size());
        assertEquals("PUT", requests.getFirst().method());
        assertEquals("/api/v1/tools/my-tool", requests.getFirst().path());
    }

    @Test
    void removeToolUsesDeleteWithPathParam() {
        client.removeTool("my-tool");

        assertEquals(1, requests.size());
        assertEquals("DELETE", requests.getFirst().method());
        assertEquals("/api/v1/tools/my-tool", requests.getFirst().path());
    }

    // ==================== Resources API Tests ====================

    @Test
    void exposeResourceUsesPostAtResourcesRoot() {
        ResourceReference resource = new ResourceReference();
        resource.setName("test-resource");

        client.exposeResource(resource);

        assertEquals(1, requests.size());
        assertEquals("POST", requests.getFirst().method());
        assertEquals("/api/v1/resources", requests.getFirst().path());
    }

    @Test
    void listResourcesUsesGetAtResourcesRoot() {
        client.listResources();

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/resources", requests.getFirst().path());
    }

    @Test
    void updateResourceUsesPutWithPathParam() {
        ResourceReference resource = new ResourceReference();
        resource.setName("my-resource");

        client.updateResource("my-resource", resource);

        assertEquals(1, requests.size());
        assertEquals("PUT", requests.getFirst().method());
        assertEquals("/api/v1/resources/my-resource", requests.getFirst().path());
    }

    @Test
    void removeResourceUsesDeleteWithPathParam() {
        client.removeResource("my-resource");

        assertEquals(1, requests.size());
        assertEquals("DELETE", requests.getFirst().method());
        assertEquals("/api/v1/resources/my-resource", requests.getFirst().path());
    }

    // ==================== Forwards API Tests ====================

    @Test
    void addForwardUsesPostAtForwardsRoot() {
        ForwardReference forward = new ForwardReference();
        forward.setName("test-forward");

        client.addForward(forward);

        assertEquals(1, requests.size());
        assertEquals("POST", requests.getFirst().method());
        assertEquals("/api/v1/forwards", requests.getFirst().path());
    }

    @Test
    void listForwardsUsesGetAtForwardsRoot() {
        client.listForwards();

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/forwards", requests.getFirst().path());
    }

    @Test
    void updateForwardUsesPutWithPathParam() {
        ForwardReference forward = new ForwardReference();
        forward.setName("my-forward");

        client.updateForward("my-forward", forward);

        assertEquals(1, requests.size());
        assertEquals("PUT", requests.getFirst().method());
        assertEquals("/api/v1/forwards/my-forward", requests.getFirst().path());
    }

    @Test
    void removeForwardUsesDeleteWithPathParam() {
        client.removeForward("my-forward");

        assertEquals(1, requests.size());
        assertEquals("DELETE", requests.getFirst().method());
        assertEquals("/api/v1/forwards/my-forward", requests.getFirst().path());
    }

    // ==================== Namespaces API Tests ====================

    @Test
    void listNamespacesUsesGetAtNamespacesRoot() {
        client.listNamespaces();

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/namespaces", requests.getFirst().path());
    }

    // ==================== DataStores API Tests ====================

    @Test
    void addDataStoreUsesPostAtDataStoreRoot() {
        client.addDataStore(new DataStore());

        assertEquals(1, requests.size());
        assertEquals("POST", requests.getFirst().method());
        assertEquals("/api/v1/data-store", requests.getFirst().path());
    }

    @Test
    void listDataStoresUsesGetAtDataStoreRoot() {
        client.listDataStores();

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/data-store", requests.getFirst().path());
    }

    @Test
    void getDataStoreByIdUsesGetWithPathParam() {
        client.getDataStoreById("ds-123");

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/data-store/ds-123", requests.getFirst().path());
    }

    @Test
    void getDataStoresByNameUsesGetWithQueryParam() {
        client.getDataStoresByName("my-store");

        assertEquals(1, requests.size());
        assertEquals("GET", requests.getFirst().method());
        assertEquals("/api/v1/data-store?name=my-store", requests.getFirst().path());
    }

    @Test
    void removeDataStoreUsesDeleteWithPathParam() {
        client.removeDataStore("ds-123");

        assertEquals(1, requests.size());
        assertEquals("DELETE", requests.getFirst().method());
        assertEquals("/api/v1/data-store/ds-123", requests.getFirst().path());
    }

    @Test
    void removeDataStoresByNameUsesDeleteWithQueryParam() {
        client.removeDataStoresByName("my-store");

        assertEquals(1, requests.size());
        assertEquals("DELETE", requests.getFirst().method());
        assertEquals("/api/v1/data-store?name=my-store", requests.getFirst().path());
    }

    // ==================== No-Auth Tests ====================

    @Test
    void noAuthClientCanListTools() throws IOException {
        HttpServer noAuthServer = HttpServer.create(new InetSocketAddress(0), 0);
        int noAuthPort = noAuthServer.getAddress().getPort();
        String noAuthBaseUrl = "http://localhost:" + noAuthPort;
        List<RequestRecord> noAuthRequests = new CopyOnWriteArrayList<>();

        noAuthServer.createContext("/api/", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes());
            noAuthRequests.add(new RequestRecord(
                    exchange.getRequestMethod(), exchange.getRequestURI().getPath(), body));

            String response = "{\"data\":null}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        noAuthServer.start();

        try {
            DefaultServiceConfig noAuthConfig = DefaultServiceConfig.Builder.newBuilder()
                    .baseUrl(noAuthBaseUrl)
                    .serializer(new JacksonSerializer())
                    .build();

            ServicesHttpClient noAuthClient = new ServicesHttpClient(noAuthConfig);
            noAuthClient.listTools();

            assertEquals(1, noAuthRequests.size());
            assertEquals("GET", noAuthRequests.getFirst().method());
            assertEquals("/api/v1/tools", noAuthRequests.getFirst().path());
        } finally {
            noAuthServer.stop(0);
        }
    }
}
