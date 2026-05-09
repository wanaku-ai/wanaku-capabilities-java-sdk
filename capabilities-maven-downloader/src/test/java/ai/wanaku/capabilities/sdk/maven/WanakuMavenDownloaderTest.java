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

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
