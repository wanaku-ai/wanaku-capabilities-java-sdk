package ai.wanaku.capabilities.sdk.discovery.config;

public final class TokenEndpoint {

    private TokenEndpoint() {}

    public static String direct(String uri) {
        return uri;
    }

    public static String fromBaseUrl(String baseUrl) {
        return baseUrl + "/protocol/openid-connect/token";
    }
}
