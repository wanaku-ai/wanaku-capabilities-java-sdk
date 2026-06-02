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
import java.util.Properties;

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
        return new GAV(GavUtil.group(parts), GavUtil.artifact(parts), GavUtil.version(parts));
    }

    /**
     * Parses a colon-separated GAV string, inferring the version from the given properties
     * when only {@code "groupId:artifactId"} is provided. Looks up the version first by
     * {@code groupId:artifactId}, then by {@code groupId} alone.
     *
     * @param gavString  the GAV string to parse (2 or 3 colon-separated parts)
     * @param properties the properties to look up versions from
     * @return the parsed GAV
     * @throws IllegalArgumentException if the format is invalid or the version cannot be resolved
     */
    public static GAV parse(String gavString, Properties properties) {
        Objects.requireNonNull(gavString, "GAV string must not be null");
        Objects.requireNonNull(properties, "properties must not be null");
        String[] parts = gavString.split(":");
        if (parts.length == 2) {
            String groupId = GavUtil.group(parts);
            String artifactId = GavUtil.artifact(parts);
            String version = properties.getProperty(groupId + ":" + artifactId);
            if (version == null) {
                version = properties.getProperty(groupId);
            }
            if (version == null) {
                throw new IllegalArgumentException(
                        "Cannot resolve version for '%s': no matching entry in properties".formatted(gavString));
            }
            return new GAV(groupId, artifactId, version.trim());
        }
        if (parts.length == 3) {
            return new GAV(GavUtil.group(parts), GavUtil.artifact(parts), GavUtil.version(parts));
        }
        throw new IllegalArgumentException(
                "Invalid GAV format: expected 'groupId:artifactId[:version]' but got '" + gavString + "'");
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }
}
