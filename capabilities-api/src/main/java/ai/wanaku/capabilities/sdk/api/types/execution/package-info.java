/**
 * Provides data types for the Wanaku Code Execution Engine API.
 * <p>
 * This package contains all the data types required for submitting code execution requests,
 * receiving execution responses, and streaming execution events via Server-Sent Events (SSE).
 * <p>
 * The main types include:
 * <ul>
 *   <li>{@link ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionRequest} - Request payload for code submission</li>
 *   <li>{@link ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionResponse} - Response containing task ID and stream URL</li>
 *   <li>{@link ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEvent} - Events streamed during execution</li>
 *   <li>{@link ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionStatus} - Execution status enumeration</li>
 *   <li>{@link ai.wanaku.capabilities.sdk.api.types.execution.CodeExecutionEventType} - Event type enumeration</li>
 *   <li>{@link ai.wanaku.capabilities.sdk.api.types.execution.Language} - Supported programming languages</li>
 * </ul>
 *
 * @since 1.0.0
 */
package ai.wanaku.capabilities.sdk.api.types.execution;
