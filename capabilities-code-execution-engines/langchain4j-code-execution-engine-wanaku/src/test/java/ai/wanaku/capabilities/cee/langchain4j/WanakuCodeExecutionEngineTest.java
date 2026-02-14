package ai.wanaku.capabilities.cee.langchain4j;

import java.util.function.Consumer;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEvent;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionRequest;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionResponse;
import ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionStatus;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.common.serializer.Serializer;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WanakuCodeExecutionEngineTest {

    @Test
    void builderRequiresServiceConfig() {
        NullPointerException exception =
                assertThrows(NullPointerException.class, () -> WanakuCodeExecutionEngine.builder()
                        .engineType("jvm")
                        .language("java")
                        .build());
        assertEquals("serviceConfig must not be null", exception.getMessage());
    }

    @Test
    void builderRequiresNonEmptyEngineType() {
        ServiceConfig config = createMockConfig();

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> WanakuCodeExecutionEngine.builder()
                        .serviceConfig(config)
                        .engineType("")
                        .language("java")
                        .build());
        assertEquals("engineType must not be null or empty", exception.getMessage());
    }

    @Test
    void builderRequiresNonNullEngineType() {
        ServiceConfig config = createMockConfig();

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> WanakuCodeExecutionEngine.builder()
                        .serviceConfig(config)
                        .engineType(null)
                        .language("java")
                        .build());
        assertEquals("engineType must not be null or empty", exception.getMessage());
    }

    @Test
    void builderRequiresNonEmptyLanguage() {
        ServiceConfig config = createMockConfig();

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> WanakuCodeExecutionEngine.builder()
                        .serviceConfig(config)
                        .engineType("jvm")
                        .language("")
                        .build());
        assertEquals("language must not be null or empty", exception.getMessage());
    }

    @Test
    void builderRequiresNonNullLanguage() {
        ServiceConfig config = createMockConfig();

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> WanakuCodeExecutionEngine.builder()
                        .serviceConfig(config)
                        .engineType("jvm")
                        .language(null)
                        .build());
        assertEquals("language must not be null or empty", exception.getMessage());
    }

    @Test
    void engineCanBeCreatedWithMockClient() {
        ServicesHttpClient mockClient = mock(ServicesHttpClient.class);

        WanakuCodeExecutionEngine engine = new WanakuCodeExecutionEngine(mockClient, "jvm", "java", 10);

        assertNotNull(engine);
    }

    @Test
    void executeReturnsOutputOnSuccess() {
        ServicesHttpClient mockClient = mock(ServicesHttpClient.class);
        String taskId = "test-task-id";

        CodeExecutionResponse execResponse = new CodeExecutionResponse(
                taskId, "http://localhost/stream", CodeExecutionStatus.PENDING, System.currentTimeMillis());
        when(mockClient.executeCode(eq("jvm"), eq("java"), any(CodeExecutionRequest.class)))
                .thenReturn(execResponse);

        doAnswer(invocation -> {
                    Consumer<CodeExecutionEvent> consumer = invocation.getArgument(4);
                    consumer.accept(CodeExecutionEvent.started(taskId));
                    consumer.accept(completedWithOutput(taskId, 0, "Hello, World!"));
                    return null;
                })
                .when(mockClient)
                .streamCodeExecutionEvents(eq("jvm"), eq("java"), eq(taskId), eq(10), any());

        WanakuCodeExecutionEngine engine = new WanakuCodeExecutionEngine(mockClient, "jvm", "java", 10);
        String result = engine.execute("System.out.println(\"Hello, World!\");");

        assertEquals("Hello, World!", result);
    }

    @Test
    void executeReturnsNullOnFailure() {
        ServicesHttpClient mockClient = mock(ServicesHttpClient.class);
        String taskId = "test-task-id";

        CodeExecutionResponse execResponse = new CodeExecutionResponse(
                taskId, "http://localhost/stream", CodeExecutionStatus.PENDING, System.currentTimeMillis());
        when(mockClient.executeCode(eq("jvm"), eq("java"), any(CodeExecutionRequest.class)))
                .thenReturn(execResponse);

        doAnswer(invocation -> {
                    Consumer<CodeExecutionEvent> consumer = invocation.getArgument(4);
                    consumer.accept(CodeExecutionEvent.started(taskId));
                    consumer.accept(CodeExecutionEvent.failed(taskId, 1, "Compilation error"));
                    return null;
                })
                .when(mockClient)
                .streamCodeExecutionEvents(eq("jvm"), eq("java"), eq(taskId), eq(10), any());

        WanakuCodeExecutionEngine engine = new WanakuCodeExecutionEngine(mockClient, "jvm", "java", 10);
        String result = engine.execute("invalid code");

        assertNull(result);
    }

    @Test
    void executeReturnsNullOnTimeout() {
        ServicesHttpClient mockClient = mock(ServicesHttpClient.class);
        String taskId = "test-task-id";

        CodeExecutionResponse execResponse = new CodeExecutionResponse(
                taskId, "http://localhost/stream", CodeExecutionStatus.PENDING, System.currentTimeMillis());
        when(mockClient.executeCode(eq("jvm"), eq("java"), any(CodeExecutionRequest.class)))
                .thenReturn(execResponse);

        doAnswer(invocation -> {
                    Consumer<CodeExecutionEvent> consumer = invocation.getArgument(4);
                    consumer.accept(CodeExecutionEvent.started(taskId));
                    consumer.accept(CodeExecutionEvent.timeout(taskId));
                    return null;
                })
                .when(mockClient)
                .streamCodeExecutionEvents(eq("jvm"), eq("java"), eq(taskId), eq(10), any());

        WanakuCodeExecutionEngine engine = new WanakuCodeExecutionEngine(mockClient, "jvm", "java", 10);
        String result = engine.execute("while(true) {}");

        assertNull(result);
    }

    @Test
    void executeReturnsOutputFromCompletedEvent() {
        ServicesHttpClient mockClient = mock(ServicesHttpClient.class);
        String taskId = "test-task-id";

        CodeExecutionResponse execResponse = new CodeExecutionResponse(
                taskId, "http://localhost/stream", CodeExecutionStatus.PENDING, System.currentTimeMillis());
        when(mockClient.executeCode(eq("jvm"), eq("java"), any(CodeExecutionRequest.class)))
                .thenReturn(execResponse);

        doAnswer(invocation -> {
                    Consumer<CodeExecutionEvent> consumer = invocation.getArgument(4);
                    consumer.accept(CodeExecutionEvent.started(taskId));
                    consumer.accept(completedWithOutput(taskId, 0, "Line 1\nLine 2\nLine 3\n"));
                    return null;
                })
                .when(mockClient)
                .streamCodeExecutionEvents(eq("jvm"), eq("java"), eq(taskId), eq(10), any());

        WanakuCodeExecutionEngine engine = new WanakuCodeExecutionEngine(mockClient, "jvm", "java", 10);
        String result = engine.execute("print lines");

        assertEquals("Line 1\nLine 2\nLine 3\n", result);
    }

    @Test
    void codeExecutionExceptionContainsExitCode() {
        CodeExecutionException exception = new CodeExecutionException("Test error", 1);

        assertEquals("Test error", exception.getMessage());
        assertEquals(1, exception.getExitCode());
    }

    @Test
    void codeExecutionExceptionAllowsNullExitCode() {
        CodeExecutionException exception = new CodeExecutionException("Test error", null);

        assertEquals("Test error", exception.getMessage());
        assertNull(exception.getExitCode());
    }

    @Test
    void codeExecutionExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Underlying error");
        CodeExecutionException exception = new CodeExecutionException("Test error", cause, 2);

        assertEquals("Test error", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(2, exception.getExitCode());
    }

    private ServiceConfig createMockConfig() {
        ServiceConfig config = mock(ServiceConfig.class);
        Serializer serializer = mock(Serializer.class);
        when(config.getBaseUrl()).thenReturn("http://localhost:8080");
        when(config.getSerializer()).thenReturn(serializer);
        return config;
    }

    private static CodeExecutionEvent completedWithOutput(String taskId, int exitCode, String output) {
        CodeExecutionEvent event = CodeExecutionEvent.completed(taskId, exitCode);
        event.setOutput(output);
        return event;
    }
}
