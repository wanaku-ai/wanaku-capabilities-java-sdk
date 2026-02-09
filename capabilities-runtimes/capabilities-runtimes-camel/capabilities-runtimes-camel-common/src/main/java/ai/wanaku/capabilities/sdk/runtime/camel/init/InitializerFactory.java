package ai.wanaku.capabilities.sdk.runtime.camel.init;

import java.nio.file.Path;

/**
 * Factory for creating initializers based on the initFrom parameter.
 * Returns a GitInitializer if a repository URL is provided, otherwise returns NoOpInitializer.
 */
public final class InitializerFactory {

    /**
     * Creates an appropriate initializer based on the initFrom parameter.
     *
     * @param initFrom Git repository URL (SSH or HTTPS), or null for no initialization
     * @param dataDir  Directory where the repository will be cloned
     * @return GitInitializer if initFrom is provided, NoOpInitializer otherwise
     */
    public static Initializer createInitializer(String initFrom, Path dataDir) {
        final String normalizedInitFrom = initFrom == null ? null : initFrom.trim();
        if (normalizedInitFrom == null || normalizedInitFrom.isEmpty()) {
            return new NoOpInitializer();
        }

        // Assume it's a Git repository URL (supports both SSH and HTTPS)
        // Examples: git@github.com:user/repo.git or https://github.com/user/repo.git
        return new GitInitializer(normalizedInitFrom, dataDir);
    }
}
