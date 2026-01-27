package ai.wanaku.capabilities.cee.langchain4j;

import ai.wanaku.capabilities.sdk.api.types.WanakuResponse;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEvent;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEventType;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionRequest;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionResponse;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;
import dev.langchain4j.code.CodeExecutionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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

    private WanakuCodeExecutionEngine(Builder builder) {
        this(new ServicesHttpClient(builder.serviceConfig), builder.engineType, builder.language);
    }

    WanakuCodeExecutionEngine(ServicesHttpClient client, String engineType, String language) {
        this.client = client;
        this.engineType = engineType;
        this.language = language;
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
        LOG.debug("Executing code via Wanaku Code Execution Engine: engineType={}, language={}",
                engineType, language);

        CodeExecutionRequest request = new CodeExecutionRequest(code);
        WanakuResponse<CodeExecutionResponse> response = client.executeCode(engineType, language, request);

        CodeExecutionResponse executionResponse = response.data();
        String taskId = executionResponse.taskId();

        LOG.debug("Code execution task submitted: taskId={}", taskId);

        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();
        ExecutionResult result = new ExecutionResult();

        client.streamCodeExecutionEvents(engineType, language, taskId, event -> {
            processEvent(event, outputBuilder, errorBuilder, result);
        });

        if (result.failed) {
            String errorMessage = errorBuilder.length() > 0
                    ? errorBuilder.toString()
                    : result.failureMessage;
            throw new CodeExecutionException(
                    "Code execution failed: " + errorMessage,
                    result.exitCode);
        }

        if (result.timeout) {
            throw new CodeExecutionException("Code execution timed out", null);
        }

        if (result.cancelled) {
            throw new CodeExecutionException("Code execution was cancelled", null);
        }

        return outputBuilder.toString();
    }

    private void processEvent(CodeExecutionEvent event, StringBuilder outputBuilder,
                              StringBuilder errorBuilder, ExecutionResult result) {
        CodeExecutionEventType eventType = event.getEventType();
        LOG.trace("Received event: type={}, taskId={}", eventType, event.getTaskId());

        switch (eventType) {
            case STARTED -> LOG.debug("Code execution started: taskId={}", event.getTaskId());
            case OUTPUT -> {
                if (event.getOutput() != null) {
                    outputBuilder.append(event.getOutput());
                }
            }
            case ERROR -> {
                if (event.getError() != null) {
                    errorBuilder.append(event.getError());
                }
            }
            case COMPLETED -> {
                result.exitCode = event.getExitCode();
                LOG.debug("Code execution completed: taskId={}, exitCode={}",
                        event.getTaskId(), event.getExitCode());
            }
            case FAILED -> {
                result.failed = true;
                result.exitCode = event.getExitCode();
                result.failureMessage = event.getMessage();
                LOG.warn("Code execution failed: taskId={}, exitCode={}, message={}",
                        event.getTaskId(), event.getExitCode(), event.getMessage());
            }
            case TIMEOUT -> {
                result.timeout = true;
                LOG.warn("Code execution timed out: taskId={}", event.getTaskId());
            }
            case CANCELLED -> {
                result.cancelled = true;
                LOG.info("Code execution cancelled: taskId={}", event.getTaskId());
            }
        }
    }

    private static class ExecutionResult {
        boolean failed = false;
        boolean timeout = false;
        boolean cancelled = false;
        Integer exitCode = null;
        String failureMessage = null;
    }

    /**
     * Builder for constructing {@link WanakuCodeExecutionEngine} instances.
     */
    public static class Builder {
        private ServiceConfig serviceConfig;
        private String engineType = "camel";
        private String language = "java";

        private Builder() {
        }

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
}
