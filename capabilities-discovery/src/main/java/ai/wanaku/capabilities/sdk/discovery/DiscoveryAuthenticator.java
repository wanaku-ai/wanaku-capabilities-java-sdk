package ai.wanaku.capabilities.sdk.discovery;

import ai.wanaku.capabilities.sdk.discovery.config.DiscoveryServiceConfig;
import ai.wanaku.capabilities.sdk.discovery.exceptions.DiscoveryAuthException;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryAuthenticator.class);
    private final DiscoveryServiceConfig config;
    private AccessToken accessToken;
    private RefreshToken refreshToken;
    private Instant creationTime;

    public DiscoveryAuthenticator(DiscoveryServiceConfig config) {
        this.config = config;

        renewToken(config);

        LOG.info("Received token with a lifetime of {} seconds", accessToken.getLifetime());
    }

    private void renewToken(DiscoveryServiceConfig config) {
        final TokenRequest request = createTokenRequest(config);
        requestToken(request);
    }

    private TokenRequest createTokenRequest(DiscoveryServiceConfig config) {
        final ClientAuthentication clientAuth = getClientAuthentication(config);

        URI tokenEndpoint = URI.create(config.getTokenEndpoint());

        TokenRequest request;
        if (refreshToken == null) {
            AuthorizationGrant clientGrant = new ClientCredentialsGrant();
            request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, null);
        } else {
            AuthorizationGrant refreshTokenGrant = new RefreshTokenGrant(refreshToken);
            request = new TokenRequest(tokenEndpoint, clientAuth, refreshTokenGrant, null);
        }

        return request;
    }

    private static ClientAuthentication getClientAuthentication(DiscoveryServiceConfig config) {
        ClientID clientID = new ClientID(config.getClientId());
        Secret clientSecret = new Secret(config.getSecret());
        return new ClientSecretBasic(clientID, clientSecret);
    }

    private void requestToken(TokenRequest request) {
        TokenResponse response = null;
        try {
            response = TokenResponse.parse(request.toHTTPRequest().send());
        } catch (IOException | ParseException e) {
            throw new DiscoveryAuthException(e);
        }

        if (!response.indicatesSuccess()) {
            // We got an error response...
            TokenErrorResponse errorResponse = response.toErrorResponse();
            LOG.error("Unable to authenticate with discovery service: {}", errorResponse.getErrorObject().getDescription());
            throw new RuntimeException(errorResponse.getErrorObject().getDescription());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();

        // Get the access token
        accessToken = successResponse.getTokens().getAccessToken();
        refreshToken = successResponse.getTokens().getRefreshToken();
        creationTime = Instant.now();
    }

    private String currentValidAccessToken() {
        final long elapsedSeconds = Duration.between(creationTime, Instant.now()).getSeconds();

        if (elapsedSeconds >= (accessToken.getLifetime() - 30)) {
            LOG.info("The token is about to expire. Renewing token to prevent that from happening ...");
            renewToken(config);
        }

        return accessToken.getValue();
    }

    public String toHeaderValue() {
        return String.format("Bearer %s", currentValidAccessToken());
    }

}
