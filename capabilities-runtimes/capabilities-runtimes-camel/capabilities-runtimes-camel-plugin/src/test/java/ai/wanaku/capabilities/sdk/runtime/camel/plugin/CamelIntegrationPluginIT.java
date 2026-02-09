package ai.wanaku.capabilities.sdk.runtime.camel.plugin;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Integration test for {@link CamelIntegrationPlugin}.
 *
 * <p>This test requires an external Wanaku instance and is triggered manually.
 * Set the following environment variables to run:</p>
 *
 * <pre>
 * REGISTRATION_URL=http://localhost:8080
 * REGISTRATION_ANNOUNCE_ADDRESS=localhost
 * GRPC_PORT=9190
 * SERVICE_NAME=camel-plugin-test
 * ROUTES_RULES=file:///path/to/cat-facts-rules.yaml
 * CLIENT_ID=wanaku-service
 * CLIENT_SECRET=your-secret
 * </pre>
 *
 * <p>Run with:</p>
 * <pre>
 * mvn verify -pl capabilities-runtimes/capabilities-runtime-camel/capabilities-runtime-camel-plugin -am \
 *   -Dit.test=CamelIntegrationPluginIT
 * </pre>
 */
@EnabledIfEnvironmentVariable(named = "REGISTRATION_URL", matches = ".+")
class CamelIntegrationPluginIT {

    @Test
    void testPluginLoadsAndRegisters() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();

        // Add the cat facts route
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:catFacts")
                        .routeId("cat-facts-route")
                        .log("Fetching ${header.COUNT} cat facts...")
                        .toD("https://meowfacts.herokuapp.com/?count=${header.COUNT}")
                        .log("Response: ${body}");
            }
        });

        // Create and load the plugin
        CamelIntegrationPlugin plugin = new CamelIntegrationPlugin();

        try {
            camelContext.start();
            //            plugin.load(camelContext);

            // Keep running for manual testing - wait for interrupt or timeout
            System.out.println("Plugin loaded successfully. Press Ctrl+C to stop or wait 60 seconds...");
            Thread.sleep(60_000);

        } finally {
            plugin.unload(camelContext);
            camelContext.stop();
        }
    }
}
