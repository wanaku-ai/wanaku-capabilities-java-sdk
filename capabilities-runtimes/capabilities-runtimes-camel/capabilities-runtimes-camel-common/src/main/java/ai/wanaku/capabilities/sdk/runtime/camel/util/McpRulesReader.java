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

package ai.wanaku.capabilities.sdk.runtime.camel.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class McpRulesReader {
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    public static McpSpec readMcpSpecFromFile(String filePath) throws IOException {
        return YAML_MAPPER.readValue(new File(filePath), McpSpec.class);
    }

    public static McpSpec readMcpSpecFromFile(File file) throws IOException {
        return YAML_MAPPER.readValue(file, McpSpec.class);
    }

    public static McpSpec readMcpSpecFromInputStream(InputStream inputStream) throws IOException {
        return YAML_MAPPER.readValue(inputStream, McpSpec.class);
    }

    public static McpSpec readMcpSpecFromString(String yamlContent) throws IOException {
        return YAML_MAPPER.readValue(yamlContent, McpSpec.class);
    }

    @Deprecated
    public static Tool readFromFile(String filePath) throws IOException {
        return YAML_MAPPER.readValue(new File(filePath), Tool.class);
    }

    @Deprecated
    public static Tool readFromFile(File file) throws IOException {
        return YAML_MAPPER.readValue(file, Tool.class);
    }

    @Deprecated
    public static Tool readFromInputStream(InputStream inputStream) throws IOException {
        return YAML_MAPPER.readValue(inputStream, Tool.class);
    }

    @Deprecated
    public static Tool readFromString(String yamlContent) throws IOException {
        return YAML_MAPPER.readValue(yamlContent, Tool.class);
    }
}
