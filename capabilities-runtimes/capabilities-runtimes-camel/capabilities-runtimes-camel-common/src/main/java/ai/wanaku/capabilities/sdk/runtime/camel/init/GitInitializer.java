package ai.wanaku.capabilities.sdk.runtime.camel.init;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializer that clones a Git repository during application startup.
 * The repository is cloned to {dataDir}/cloned-repo and reused if it already exists.
 */
public class GitInitializer implements Initializer {
    private static final Logger LOG = LoggerFactory.getLogger(GitInitializer.class);
    private static final String CLONED_REPO_DIR_NAME = "cloned-repo";

    private final String gitRepoUrl;
    private final Path dataDir;
    private Path clonedRepoPath;

    public GitInitializer(String gitRepoUrl, Path dataDir) {
        this.gitRepoUrl = gitRepoUrl;
        this.dataDir = dataDir;
    }

    @Override
    public void initialize() throws GitAPIException, IOException {
        clonedRepoPath = dataDir.resolve(CLONED_REPO_DIR_NAME);
        File clonedRepoDir = clonedRepoPath.toFile();

        if (clonedRepoDir.exists()) {
            try (Git ignored = Git.open(clonedRepoDir)) {
                LOG.info("Reusing existing cloned repository at {}", clonedRepoPath);
                return;
            } catch (IOException e) {
                throw new IOException(
                        "Existing cloned repository at " + clonedRepoPath + " is not a valid Git repository", e);
            }
        }

        LOG.info("Cloning git repository from {} to {}", gitRepoUrl, clonedRepoPath);

        try (Git git = Git.cloneRepository()
                .setURI(gitRepoUrl)
                .setDirectory(clonedRepoDir)
                .call()) {
            LOG.info("Successfully cloned repository from {}", gitRepoUrl);
        }
    }

    public Path getClonedRepoPath() {
        return clonedRepoPath;
    }
}
