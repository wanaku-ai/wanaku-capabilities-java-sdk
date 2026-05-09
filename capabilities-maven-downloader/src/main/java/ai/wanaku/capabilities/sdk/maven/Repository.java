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

import java.net.URI;
import java.util.Objects;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * A Maven remote repository identified by an ID and URL.
 *
 * @param id  the repository identifier
 * @param url the repository URL
 */
public record Repository(String id, String url) {

    public Repository {
        Objects.requireNonNull(url, "Repository URL must not be null");
        if (id == null || id.isBlank()) {
            id = deriveId(url);
        }
    }

    RemoteRepository toRemoteRepository() {
        return new RemoteRepository.Builder(id, "default", url).build();
    }

    private static String deriveId(String url) {
        String host = URI.create(url).getHost();
        return host != null ? host : "repo";
    }
}
