package ai.wanaku.capabilities.sdk.services;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import ai.wanaku.capabilities.sdk.api.types.DataStore;
import ai.wanaku.capabilities.sdk.common.config.DefaultServiceConfig;
import ai.wanaku.capabilities.sdk.common.serializer.JacksonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicesHttpClientCustomizationTest {
    private final ObjectMapper json = new ObjectMapper();
    private final AtomicReference<String> response = new AtomicReference<>("{\"data\":null}");
    private final List<String> authorization = new CopyOnWriteArrayList<>();
    private HttpServer server;
    private String baseUrl;
    private ServicesHttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        server.createContext("/api/", exchange -> {
            authorization.add(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.getRequestBody().readAllBytes();
            byte[] body = response.get().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        client = new ServicesHttpClient(configuration(), HttpClient.newHttpClient(), builder -> builder.setHeader(
                        "Authorization", "Bearer static-token")
                .timeout(Duration.ofSeconds(2)));
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private DefaultServiceConfig configuration() {
        return DefaultServiceConfig.Builder.newBuilder()
                .baseUrl(baseUrl)
                .serializer(new JacksonSerializer())
                .build();
    }

    @Test
    void customizesRequestsForEveryHttpOperation() {
        client.listDataStores();
        client.addDataStore(new DataStore());
        client.updateTool("test", new ai.wanaku.capabilities.sdk.api.types.ToolReference());
        client.removeDataStore("test");
        assertEquals(
                List.of("Bearer static-token", "Bearer static-token", "Bearer static-token", "Bearer static-token"),
                authorization);
    }

    @Test
    void usesTheSuppliedHttpClient() {
        server.createContext("/api/v1/service-catalog/download", exchange -> {
            exchange.getResponseHeaders().set("Location", "/api/redirected");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        response.set("{\"data\":{\"name\":\"catalog\",\"data\":\"emlw\"}}");
        var redirected = new ServicesHttpClient(
                configuration(),
                HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build(),
                builder -> builder);
        assertEquals("catalog", redirected.getServiceCatalog("catalog").data().getName());
    }

    @Test
    void honorsTheCustomizedRequestTimeout() {
        server.createContext("/api/v1/service-catalog/download", exchange -> {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        var timed = new ServicesHttpClient(
                configuration(), HttpClient.newHttpClient(), builder -> builder.timeout(Duration.ofMillis(100)));
        var error = assertThrows(WanakuException.class, () -> timed.getServiceCatalog("catalog"));
        assertInstanceOf(HttpTimeoutException.class, error.getCause());
    }

    @Test
    void readsCatalogFormsAndIgnoresOnlyUnknownCatalogMetadata() throws Exception {
        Map<String, Object> store = Map.of("id", "id", "name", "catalog", "data", "emlw", "future", true);
        for (Object form : List.of(store, List.of(store), Map.of("data", store), Map.of("data", List.of(store)))) {
            response.set(json.writeValueAsString(form));
            DataStore catalog = client.getServiceCatalog("catalog").data();
            assertEquals("catalog", catalog.getName());
            assertEquals("emlw", catalog.getData());
        }
        response.set("{\"data\":null,\"future\":true}");
        assertThrows(WanakuException.class, client::listDataStores);
    }

    @Test
    void rejectsMissingAmbiguousAndMalformedCatalogs() {
        for (String body : List.of(
                "null",
                "{}",
                "[]",
                "{\"data\":null}",
                "{\"data\":{}}",
                "{\"data\":42}",
                "{\"data\":[{},{}]}",
                "{\"data\":{\"data\":{}}}",
                "{\"data\":{\"data\":\"emlw\"},\"error\":{\"message\":\"error\"}}",
                "invalid")) {
            response.set(body);
            assertThrows(WanakuException.class, () -> client.getServiceCatalog("catalog"), body);
        }
    }

    @Test
    void rejectsNullTransportAndCustomizer() {
        assertThrows(
                NullPointerException.class, () -> new ServicesHttpClient(configuration(), null, builder -> builder));
        assertThrows(
                NullPointerException.class,
                () -> new ServicesHttpClient(configuration(), HttpClient.newHttpClient(), null));
    }
}
