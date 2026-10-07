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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WanakuMavenDownloaderTest {

    @TempDir
    Path tempRepo;

    @Test
    void constructionWithEmptyReposSucceeds() {
        try (WanakuMavenDownloader downloader = new WanakuMavenDownloader(Collections.emptyList(), tempRepo)) {
            assertNotNull(downloader.getClassLoader());
        }
    }

    @Test
    void closeDoesNotThrow() {
        WanakuMavenDownloader downloader = new WanakuMavenDownloader(Collections.emptyList(), tempRepo);
        assertDoesNotThrow(downloader::close);
    }

    @Test
    void downloadResolvesArtifactAndTransitiveDependencies() {
        try (WanakuMavenDownloader downloader = new WanakuMavenDownloader(Collections.emptyList(), tempRepo)) {
            List<Path> paths = downloader.download(List.of(GAV.parse("org.apache.commons:commons-lang3:3.14.0")));

            assertFalse(paths.isEmpty(), "Expected at least one resolved JAR");

            assertDoesNotThrow(
                    () -> downloader.getClassLoader().loadClass("org.apache.commons.lang3.StringUtils"),
                    "commons-lang3 class should be loadable");
        }
    }

    @Test
    void downloadThrowsOnInvalidArtifact() {
        try (WanakuMavenDownloader downloader = new WanakuMavenDownloader(Collections.emptyList(), tempRepo)) {
            assertThrows(
                    DependencyDownloadException.class,
                    () -> downloader.download(List.of(GAV.parse("com.nonexistent:does-not-exist:999.999.999"))));
        }
    }

    @Test
    void directDependencyWinsOverAnEarlierRootTransitiveVersion() throws Exception {
        install(
                GAV.parse("example.fixture:expert:1.0"),
                "expert.marker",
                "expert",
                "<dependency><groupId>example.fixture</groupId><artifactId>helper</artifactId><version>1.0</version></dependency>");
        install(GAV.parse("example.fixture:helper:1.0"), "version.marker", "1.0", "");
        install(GAV.parse("example.fixture:helper:2.0"), "version.marker", "2.0", "");
        try (WanakuMavenDownloader downloader = new WanakuMavenDownloader(List.of(), tempRepo)) {
            List<Path> paths = downloader.download(
                    List.of(GAV.parse("example.fixture:expert:1.0"), GAV.parse("example.fixture:helper:2.0")));
            assertEquals(
                    Set.of("expert-1.0.jar", "helper-2.0.jar"),
                    paths.stream().map(path -> path.getFileName().toString()).collect(Collectors.toSet()));
            assertThrows(UnsupportedOperationException.class, () -> paths.add(tempRepo));
            try (var version = downloader.getClassLoader().getResourceAsStream("version.marker")) {
                assertNotNull(version);
                assertEquals("2.0", new String(version.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    void emptyAndNullDeclarationsRetainTheirContracts() {
        try (WanakuMavenDownloader downloader = new WanakuMavenDownloader(List.of(), tempRepo)) {
            assertTrue(downloader.download(List.of()).isEmpty());
            assertThrows(NullPointerException.class, () -> downloader.download(null));
        }
    }

    private void install(GAV gav, String resource, String contents, String dependencies) throws IOException {
        Path artifactDirectory =
                Files.createDirectories(tempRepo.resolve(gav.groupId().replace('.', '/'))
                        .resolve(gav.artifactId())
                        .resolve(gav.version()));
        String base = gav.artifactId() + "-" + gav.version();
        Files.writeString(
                artifactDirectory.resolve(base + ".pom"),
                "<project><modelVersion>4.0.0</modelVersion><groupId>" + gav.groupId() + "</groupId><artifactId>"
                        + gav.artifactId() + "</artifactId><version>" + gav.version()
                        + "</version><dependencies>" + dependencies + "</dependencies></project>");
        try (var jar = new JarOutputStream(Files.newOutputStream(artifactDirectory.resolve(base + ".jar")))) {
            jar.putNextEntry(new JarEntry(resource));
            jar.write(contents.getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
        }
    }
}
