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

import java.util.Objects;

/**
 * An immutable Maven coordinate consisting of group ID, artifact ID, and version.
 *
 * @param groupId    the Maven group ID
 * @param artifactId the Maven artifact ID
 * @param version    the Maven version
 */
public record GAV(String groupId, String artifactId, String version) {

    public GAV {
        Objects.requireNonNull(groupId, "groupId must not be null");
        Objects.requireNonNull(artifactId, "artifactId must not be null");
        Objects.requireNonNull(version, "version must not be null");
    }

    /**
     * Parses a colon-separated GAV string (e.g. {@code "org.example:my-lib:1.0"}).
     *
     * @param gavString the GAV string to parse
     * @return the parsed GAV
     * @throws IllegalArgumentException if the string does not contain exactly three colon-separated parts
     */
    public static GAV parse(String gavString) {
        Objects.requireNonNull(gavString, "GAV string must not be null");
        String[] parts = gavString.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid GAV format: expected 'groupId:artifactId:version' but got '" + gavString + "'");
        }
        return new GAV(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }
}
