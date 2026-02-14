package ai.wanaku.capabilities.cee.langchain4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEvent;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEventType;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionRequest;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionResponse;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;
import dev.langchain4j.code.CodeExecutionEngine;

/**
 * LangChain4j {@link CodeExecutionEngine} implementation that executes code via the Wanaku
 * Code Execution Engine API.
 * <p>
 * This implementation submits code to the Wanaku service, streams execution events via SSE,
 * and collects the output to return as the execution result.
 * <p>
 * Example usage:
 * <pre>{@code
 * ServiceConfig config = new DefaultServiceConfig("http://localhost:8080");
 * CodeExecutionEngine engine = WanakuCodeExecutionEngine.builder()
 *     .serviceConfig(config)
 *     .engineType("jvm")
 *     .language("java")
 *     .build();
 *
 * String result = engine.execute("System.out.println(\"Hello, World!\");");
 * }</pre>
 *
 * @since 1.0.0
 */
public class WanakuCodeExecutionEngine implements CodeExecutionEngine {
    private static final Logger LOG = LoggerFactory.getLogger(WanakuCodeExecutionEngine.class);

    private final ServicesHttpClient client;
    private final String engineType;
    private final String language;
    private List<List<CodeExecutionEvent>> codeEvents = new ArrayList<>();
    private final int taskTimeout;

    private WanakuCodeExecutionEngine(Builder builder) {
        this(
                new ServicesHttpClient(builder.serviceConfig),
                builder.engineType,
                builder.language,
                builder().taskTimeout);
    }

    WanakuCodeExecutionEngine(ServicesHttpClient client, String engineType, String language, int taskTimeout) {
        this.client = client;
        this.engineType = engineType;
        this.language = language;
        this.taskTimeout = taskTimeout;
    }

    /**
     * Creates a new builder for constructing a {@link WanakuCodeExecutionEngine}.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String execute(String code) {
        LOG.debug("Executing code via Wanaku Code Execution Engine: engineType={}, language={}", engineType, language);

        CodeExecutionRequest request = new CodeExecutionRequest(code);
        CodeExecutionResponse response = client.executeCode(engineType, language, request);

        String taskId = response.taskId();

        LOG.debug("Code execution task submitted: taskId={}", taskId);

        List<CodeExecutionEvent> events = new ArrayList<>();

        try {
            client.streamCodeExecutionEvents(engineType, language, taskId, taskTimeout, events::add);
        } finally {
            LOG.debug("Adding {} events to a new entry on the events list", events.size());
            codeEvents.add(events);
        }

        for (var event : events) {
            if (event.getEventType() == CodeExecutionEventType.COMPLETED) {
                return event.getOutput();
            }
        }

        LOG.warn("There was no successful code execution event reported");
        return null;
    }

    public ServicesHttpClient getClient() {
        return client;
    }

    /**
     * Builder for constructing {@link WanakuCodeExecutionEngine} instances.
     */
    public static class Builder {
        private ServiceConfig serviceConfig;
        private String engineType = "camel";
        private String language = "java";
        private int taskTimeout = 30;

        private Builder() {}

        /**
         * Sets the service configuration for connecting to the Wanaku service.
         *
         * @param serviceConfig the service configuration (required)
         * @return this builder instance
         */
        public Builder serviceConfig(ServiceConfig serviceConfig) {
            this.serviceConfig = serviceConfig;
            return this;
        }

        /**
         * Sets the execution engine type.
         *
         * @param engineType the engine type (e.g., "camel", "groovy"); defaults to "camel"
         * @return this builder instance
         */
        public Builder engineType(String engineType) {
            this.engineType = engineType;
            return this;
        }

        /**
         * Sets the programming language for code execution.
         *
         * @param language the language (e.g., "java", "groovy"); defaults to "java"
         * @return this builder instance
         */
        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder taskTimeout(int taskTimeout) {
            this.taskTimeout = taskTimeout;
            return this;
        }

        /**
         * Builds the {@link WanakuCodeExecutionEngine} instance.
         *
         * @return a new WanakuCodeExecutionEngine instance
         * @throws NullPointerException if serviceConfig is null
         * @throws IllegalArgumentException if engineType or language is null or empty
         */
        public WanakuCodeExecutionEngine build() {
            Objects.requireNonNull(serviceConfig, "serviceConfig must not be null");
            if (engineType == null || engineType.trim().isEmpty()) {
                throw new IllegalArgumentException("engineType must not be null or empty");
            }
            if (language == null || language.trim().isEmpty()) {
                throw new IllegalArgumentException("language must not be null or empty");
            }
            return new WanakuCodeExecutionEngine(this);
        }
    }

    public List<List<CodeExecutionEvent>> getCodeEvents() {
        return Collections.unmodifiableList(codeEvents);
    }
}
