package ai.wanaku.capabilities.sdk.api.util;

/**
 * Reserved argument names used in the Wanaku tool invocation protocol.
 * <p>
 * This utility class defines constant names and prefixes that have special meaning
 * when processing tool arguments. These reserved names allow for metadata injection
 * and body content handling without requiring explicit tool configuration.
 */
public final class ReservedArgumentNames {

    /**
     * Reserved argument name for the request body content.
     * <p>
     * When a tool argument uses this name, its value is extracted and set as the
     * body of the tool invocation request rather than being passed as a regular argument.
     */
    public static final String BODY = "wanaku_body";

    /**
     * Prefix for metadata arguments that should be converted to headers.
     * <p>
     * Arguments with this prefix (e.g., {@code wanaku_meta_contextId}) are extracted
     * from the regular arguments, the prefix is stripped, and the remaining name
     * becomes the header name (e.g., {@code contextId}).
     * <p>
     * This allows AI services to inject headers into tool invocations without
     * requiring changes to the tool's configuration or route definition.
     * <p>
     * Example usage in LangChain4j AI Service:
     * <pre>{@code
     * @RegisterAiService
     * public interface MyService {
     *     @McpToolBox("toolbox")
     *     String callTool(
     *         @Header("wanaku_meta_contextId") String contextId,
     *         @Header("wanaku_meta_userId") String userId,
     *         @UserMessage String message
     *     );
     * }
     * }</pre>
     * <p>
     * In the tool implementation, these become accessible as headers:
     * <pre>{@code
     * Map<String, String> headers = request.getHeadersMap();
     * String contextId = headers.get("contextId");  // prefix stripped
     * String userId = headers.get("userId");
     * }</pre>
     */
    public static final String METADATA_PREFIX = "wanaku_meta_";

    private ReservedArgumentNames() {}
}
