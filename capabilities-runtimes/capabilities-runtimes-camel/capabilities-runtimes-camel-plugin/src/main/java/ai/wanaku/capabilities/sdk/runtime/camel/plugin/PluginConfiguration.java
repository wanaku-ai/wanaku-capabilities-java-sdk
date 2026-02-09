package ai.wanaku.capabilities.sdk.runtime.camel.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration loader for the Camel Integration Plugin.
 * Loads configuration from properties file with environment variable override.
 * Environment variables take precedence over properties file values.
 */
public class PluginConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(PluginConfiguration.class);
    private static final String PROPERTIES_FILE = "camel-integration-capability.properties";

    private String registrationUrl;
    private int grpcPort = 9190;
    private String registrationAnnounceAddress = "auto";
    private String serviceName = "camel";
    private String routesRef;
    private String rulesRef;
    private String tokenEndpoint;
    private String clientId;
    private String clientSecret;
    private String dependenciesRef;
    private String repositoriesList;
    private String dataDir = "/tmp";
    private String initFrom;
    private int retries = 12;
    private int retryWaitSeconds = 5;
    private long initialDelay = 5;
    private long period = 5;
    private boolean noWait = false;

    /**
     * Load configuration from properties file and environment variables.
     * Environment variables take precedence.
     *
     * @return configured PluginConfiguration instance
     */
    public static PluginConfiguration load() {
        PluginConfiguration config = new PluginConfiguration();

        // Load from properties file first
        Properties props = new Properties();
        try (InputStream is = PluginConfiguration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                props.load(is);
                LOG.debug("Loaded configuration from {}", PROPERTIES_FILE);
            } else {
                LOG.debug("No {} found in classpath, using defaults and environment variables", PROPERTIES_FILE);
            }
        } catch (IOException e) {
            LOG.warn("Failed to load {}: {}", PROPERTIES_FILE, e.getMessage());
        }

        // Apply properties then override with environment variables
        config.registrationUrl = getConfigValue(props, "registration.url", "REGISTRATION_URL", null);
        config.grpcPort = Integer.parseInt(getConfigValue(props, "grpc.port", "GRPC_PORT", "9190"));
        config.registrationAnnounceAddress =
                getConfigValue(props, "registration.announce.address", "REGISTRATION_ANNOUNCE_ADDRESS", "auto");
        config.serviceName = getConfigValue(props, "service.name", "SERVICE_NAME", "camel");
        config.routesRef = getConfigValue(props, "routes.ref", "ROUTES_PATH", null);
        config.rulesRef = getConfigValue(props, "rules.ref", "ROUTES_RULES", null);
        config.tokenEndpoint = getConfigValue(props, "token.endpoint", "TOKEN_ENDPOINT", null);
        config.clientId = getConfigValue(props, "client.id", "CLIENT_ID", null);
        config.clientSecret = getConfigValue(props, "client.secret", "CLIENT_SECRET", null);
        config.dependenciesRef = getConfigValue(props, "dependencies", "DEPENDENCIES", null);
        config.repositoriesList = getConfigValue(props, "repositories", "REPOSITORIES", null);
        config.dataDir = getConfigValue(props, "data.dir", "DATA_DIR", "/tmp");
        config.initFrom = getConfigValue(props, "init.from", "INIT_FROM", null);
        config.retries = Integer.parseInt(getConfigValue(props, "retries", "RETRIES", "12"));
        config.retryWaitSeconds =
                Integer.parseInt(getConfigValue(props, "retry.wait.seconds", "RETRY_WAIT_SECONDS", "5"));
        config.initialDelay = Long.parseLong(getConfigValue(props, "initial.delay", "INITIAL_DELAY", "5"));
        config.period = Long.parseLong(getConfigValue(props, "period", "PERIOD", "5"));
        config.noWait = Boolean.parseBoolean(getConfigValue(props, "no.wait", "NO_WAIT", "false"));

        return config;
    }

    private static String getConfigValue(Properties props, String propertyKey, String envKey, String defaultValue) {
        // Environment variable takes precedence
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        // Then properties file
        String propValue = props.getProperty(propertyKey);
        if (propValue != null && !propValue.isEmpty()) {
            return propValue;
        }

        // Finally default
        return defaultValue;
    }

    /**
     * Validate that required configuration is present.
     *
     * @throws IllegalStateException if required configuration is missing
     */
    public void validate() {
        if (registrationUrl == null || registrationUrl.isEmpty()) {
            throw new IllegalStateException("registration.url (or REGISTRATION_URL env var) is required");
        }
        if (routesRef == null || routesRef.isEmpty()) {
            LOG.info("No routes or routes ref found, using builtin routes from the current integration application");
        }
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalStateException("client.id (or CLIENT_ID env var) is required");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalStateException("client.secret (or CLIENT_SECRET env var) is required");
        }
    }

    // Getters
    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public int getGrpcPort() {
        return grpcPort;
    }

    public String getRegistrationAnnounceAddress() {
        return registrationAnnounceAddress;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getRoutesRef() {
        return routesRef;
    }

    public String getRulesRef() {
        return rulesRef;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getDependenciesRef() {
        return dependenciesRef;
    }

    public String getRepositoriesList() {
        return repositoriesList;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getInitFrom() {
        return initFrom;
    }

    public int getRetries() {
        return retries;
    }

    public int getRetryWaitSeconds() {
        return retryWaitSeconds;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public long getPeriod() {
        return period;
    }

    public boolean isNoWait() {
        return noWait;
    }
}
