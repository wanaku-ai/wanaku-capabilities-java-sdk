/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.wanaku.capabilities.sdk.maven;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.supplier.SessionBuilderSupplier;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Downloads Maven dependencies (including transitive ones) at runtime and makes them
 * available via a classloader.
 *
 * <pre>{@code
 * try (WanakuMavenDownloader downloader = new WanakuMavenDownloader()) {
 *     downloader.download(List.of(GAV.parse("org.example:my-lib:1.0")));
 *     downloader.applyToCurrentThread();
 *     // classes from my-lib and its dependencies are now loadable
 * }
 * }</pre>
 */
public class WanakuMavenDownloader implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(WanakuMavenDownloader.class);

    private static final RemoteRepository CENTRAL =
            new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build();

    private final RepositorySystem repositorySystem;
    private final RepositorySystemSession.CloseableSession session;
    private final List<RemoteRepository> repositories;
    private final DynamicClassLoader classLoader;

    /**
     * Creates a downloader that resolves from Maven Central only, using {@code ~/.m2/repository}
     * as the local repository.
     */
    public WanakuMavenDownloader() {
        this(Collections.emptyList());
    }

    /**
     * Creates a downloader that resolves from Maven Central plus the given extra repositories,
     * using {@code ~/.m2/repository} as the local repository.
     *
     * @param extraRepositories additional remote repositories to resolve from
     */
    public WanakuMavenDownloader(List<Repository> extraRepositories) {
        this(extraRepositories, defaultLocalRepo());
    }

    /**
     * Creates a downloader with full control over repositories and local cache path.
     *
     * @param extraRepositories additional remote repositories to resolve from
     * @param localRepository   path to the local Maven repository cache
     */
    public WanakuMavenDownloader(List<Repository> extraRepositories, Path localRepository) {
        Objects.requireNonNull(extraRepositories, "extraRepositories must not be null");
        Objects.requireNonNull(localRepository, "localRepository must not be null");

        this.repositorySystem = new RepositorySystemSupplier().get();
        this.session = new SessionBuilderSupplier(repositorySystem)
                .get()
                .withLocalRepositoryBaseDirectories(localRepository)
                .build();

        List<RemoteRepository> repos = new ArrayList<>();
        repos.add(CENTRAL);
        for (Repository repo : extraRepositories) {
            repos.add(repo.toRemoteRepository());
        }
        this.repositories = Collections.unmodifiableList(repos);

        this.classLoader = new DynamicClassLoader(WanakuMavenDownloader.class.getClassLoader());
    }

    /**
     * Downloads the given artifacts and all their transitive compile-scope dependencies.
     *
     * @param gavs the Maven coordinates to resolve
     * @return paths to all resolved JAR files (including transitive dependencies)
     * @throws DependencyDownloadException if any dependency cannot be resolved
     */
    public List<Path> download(List<GAV> gavs) {
        Objects.requireNonNull(gavs, "gavs must not be null");

        DependencyFilter filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE, JavaScopes.RUNTIME);
        List<Path> allPaths = new ArrayList<>();

        for (GAV gav : gavs) {
            LOG.debug("Resolving {}", gav);

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(new Dependency(
                    new DefaultArtifact(gav.groupId(), gav.artifactId(), "jar", gav.version()), "compile"));
            collectRequest.setRepositories(repositories);

            DependencyRequest request = new DependencyRequest(collectRequest, filter);

            DependencyResult result;
            try {
                result = repositorySystem.resolveDependencies(session, request);
            } catch (DependencyResolutionException e) {
                throw new DependencyDownloadException("Failed to resolve " + gav, e);
            }

            for (ArtifactResult ar : result.getArtifactResults()) {
                Path jarPath = ar.getArtifact().getPath();
                if (jarPath != null) {
                    allPaths.add(jarPath);
                    classLoader.addJar(jarPath);
                    LOG.debug("  resolved {} -> {}", ar.getArtifact(), jarPath);
                }
            }
        }

        return Collections.unmodifiableList(allPaths);
    }

    /**
     * Returns the classloader containing all downloaded artifacts.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the current thread's context classloader to include all downloaded artifacts.
     */
    public void applyToCurrentThread() {
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    @Override
    public void close() {
        session.close();
        repositorySystem.close();
    }

    private static Path defaultLocalRepo() {
        return Path.of(System.getProperty("user.home"), ".m2", "repository");
    }

    private static final class DynamicClassLoader extends URLClassLoader {

        DynamicClassLoader(ClassLoader parent) {
            super(new URL[0], parent);
        }

        void addJar(Path jarPath) {
            try {
                addURL(jarPath.toUri().toURL());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid JAR path: " + jarPath, e);
            }
        }
    }
}
