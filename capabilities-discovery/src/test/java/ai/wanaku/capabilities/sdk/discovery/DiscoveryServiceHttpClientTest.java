package ai.wanaku.capabilities.sdk.discovery;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.common.config.DefaultServiceConfig;
import ai.wanaku.capabilities.sdk.common.serializer.JacksonSerializer;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscoveryServiceHttpClientTest {

    private HttpServer server;
    private DiscoveryServiceHttpClient client;
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

        // Discovery API endpoint
        server.createContext("/api/v1/management/discovery", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes());
            requests.add(new RequestRecord(
                    exchange.getRequestMethod(), exchange.getRequestURI().getPath(), body));

            String response = "{\"data\":{\"id\":\"test-id\",\"serviceName\":\"test\"}}";
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

        client = new DiscoveryServiceHttpClient(config);
    }

    @AfterEach
    void tearDown() {
        requests.clear();
        server.stop(0);
    }

    @Test
    void registerUsesPostAtBasePath() {
        ServiceTarget target = ServiceTarget.newEmptyTarget("test-service", "localhost", 9090, "tool-invoker");

        HttpResponse<String> response = client.register(target);

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals(1, requests.size());
        assertEquals("POST", requests.getFirst().method());
        assertEquals("/api/v1/management/discovery", requests.getFirst().path());
    }

    @Test
    void deregisterUsesDeleteAtBasePath() {
        ServiceTarget target = ServiceTarget.newEmptyTarget("test-service", "localhost", 9090, "tool-invoker");

        HttpResponse<String> response = client.deregister(target);

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals(1, requests.size());
        assertEquals("DELETE", requests.getFirst().method());
        assertEquals("/api/v1/management/discovery", requests.getFirst().path());
    }

    @Test
    void pingUsesHeartbeatsPath() {
        HttpResponse<String> response = client.ping("test-id");

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals(1, requests.size());
        assertEquals("POST", requests.getFirst().method());
        assertEquals(
                "/api/v1/management/discovery/heartbeats", requests.getFirst().path());
    }
}
