package ai.wanaku.capabilities.sdk.config.provider.api;

/**
 * A no-operation implementation of {@link SecretStore} that returns empty results.
 * <p>
 * This implementation extends {@link NoopConfigStore} to provide the same
 * no-operation behavior for secret storage. It is useful as a default or
 * placeholder when no actual secret storage is available or needed.
 *
 * @see NoopConfigStore
 * @see SecretStore
 */
public class NoopSecretStore extends NoopConfigStore implements SecretStore {}
