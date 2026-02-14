package ai.wanaku.capabilities.cee.langchain4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import ai.wanaku.capabilities.sdk.common.config.DefaultServiceConfig;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.common.serializer.JacksonSerializer;
import ai.wanaku.capabilities.sdk.security.TokenEndpoint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Manual integration test for {@link WanakuCodeExecutionEngine}.
 * <p>
 * This test requires a running Wanaku server and valid credentials.
 * Run with system properties:
 * <pre>
 * mvn test -Dtest=WanakuCodeExecutionEngineIT \
 *     -Dengine.test.file=/path/to/code.java \
 *     -Dengine.test.server=http://localhost:8080 \
 *     -Dengine.test.clientId=wanaku-service \
 *     -Dengine.test.secret=your-secret
 * </pre>
 * <p>
 * Optional properties:
 * <ul>
 *   <li>{@code -Dengine.test.engineType=camel} - Engine type (default: camel)</li>
 *   <li>{@code -Dengine.test.language=java} - Language (default: java)</li>
 *   <li>{@code -Dengine.test.tokenEndpoint=...} - Custom token endpoint URL</li>
 * </ul>
 */
class WanakuCodeExecutionEngineIT {

    private static final String PROP_FILE = "engine.test.file";
    private static final String PROP_SERVER = "engine.test.server";
    private static final String PROP_CLIENT_ID = "engine.test.clientId";
    private static final String PROP_SECRET = "engine.test.secret";
    private static final String PROP_ENGINE_TYPE = "engine.test.engineType";
    private static final String PROP_LANGUAGE = "engine.test.language";
    private static final String PROP_TOKEN_ENDPOINT = "engine.test.tokenEndpoint";

    @Test
    @EnabledIfSystemProperty(named = PROP_FILE, matches = ".+")
    void executeCodeFromFile() throws IOException {
        String filePath = System.getProperty(PROP_FILE);
        String serverAddress = System.getProperty(PROP_SERVER, "http://localhost:8080");
        String clientId = System.getProperty(PROP_CLIENT_ID, "wanaku-service");
        String secret = System.getProperty(PROP_SECRET);
        String engineType = System.getProperty(PROP_ENGINE_TYPE, "camel");
        String language = System.getProperty(PROP_LANGUAGE, "yaml");
        String tokenEndpointUri = System.getProperty(PROP_TOKEN_ENDPOINT);

        // Read code from file
        String code = Files.readString(Path.of(filePath));
        System.out.println("=== Code to execute ===");
        System.out.println(code);
        System.out.println("=======================");

        // Build service configuration
        ServiceConfig serviceConfig = DefaultServiceConfig.builder()
                .baseUrl(serverAddress)
                .serializer(new JacksonSerializer())
                .clientId(clientId)
                .tokenEndpoint(TokenEndpoint.autoResolve(serverAddress, tokenEndpointUri))
                .secret(secret)
                .build();

        // Create engine
        WanakuCodeExecutionEngine engine = WanakuCodeExecutionEngine.builder()
                .serviceConfig(serviceConfig)
                .engineType(engineType)
                .language(language)
                .build();

        // Execute code
        System.out.println("Executing code via Wanaku Code Execution Engine...");
        System.out.println("Server: " + serverAddress);
        System.out.println("Engine type: " + engineType);
        System.out.println("Language: " + language);
        System.out.println();

        String result = engine.execute(code);

        System.out.println("=== Execution Result ===");
        System.out.println(result);
        System.out.println("========================");

        assertNotNull(result, "Execution result should not be null");
    }
}
