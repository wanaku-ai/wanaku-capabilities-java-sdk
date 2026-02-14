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

import java.io.InputStream;
import java.util.Map;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Mapping;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Property;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class McpRulesReaderTest {

    @Test
    void testLoadSampleMcpSpec() throws Exception {
        // Load the sample file from test resources
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-tool-spec.yaml");
        assertNotNull(inputStream, "Sample MCP spec file should exist in test resources");

        // Read the YAML file
        McpSpec mcpSpec = McpRulesReader.readMcpSpecFromInputStream(inputStream);

        assertNotNull(mcpSpec, "McpSpec should not be null");
        assertNotNull(mcpSpec.getMcp(), "Mcp content should not be null");

        // Verify the tools map
        assertNotNull(mcpSpec.getMcp().getTools(), "Tools wrapper should not be null");
        Map<String, Definition> tools = mcpSpec.getMcp().getTools().getDefinitions();
        assertNotNull(tools, "Tools map should not be null");
        assertEquals(2, tools.size(), "Should have 2 tools defined");

        // Verify initiate-employee-promotion tool
        assertTrue(tools.containsKey("initiate-employee-promotion"), "Should contain initiate-employee-promotion tool");
        Definition initiatePromotion = tools.get("initiate-employee-promotion");
        assertNotNull(initiatePromotion, "initiate-employee-promotion should not be null");
        assertEquals("Initiate the promotion process for an employee", initiatePromotion.getDescription());
        assertEquals("direct:initiate-promotion", initiatePromotion.getRoute().getUri());

        // Verify properties of initiate-employee-promotion
        assertNotNull(initiatePromotion.getProperties(), "Properties should not be null");
        assertEquals(1, initiatePromotion.getProperties().size(), "Should have 1 property");

        Property employeeProperty = initiatePromotion.getProperties().get(0);
        assertEquals("employee", employeeProperty.getName());
        assertEquals("string", employeeProperty.getType());
        assertEquals("The employee to promote", employeeProperty.getDescription());
        assertTrue(employeeProperty.isRequired(), "employee property should be required");

        // Verify confirm-employee-promotion tool
        assertTrue(tools.containsKey("confirm-employee-promotion"), "Should contain confirm-employee-promotion tool");
        Definition confirmPromotion = tools.get("confirm-employee-promotion");
        assertNotNull(confirmPromotion, "confirm-employee-promotion should not be null");
        assertEquals("Confirm the promotion of an an employee", confirmPromotion.getDescription());
        assertEquals("direct:confirm-promotion", confirmPromotion.getRoute().getUri());

        // Verify properties of confirm-employee-promotion
        assertNotNull(confirmPromotion.getProperties(), "Properties should not be null");
        assertEquals(1, confirmPromotion.getProperties().size(), "Should have 1 property");

        Property confirmEmployeeProperty = confirmPromotion.getProperties().get(0);
        assertEquals("employee", confirmEmployeeProperty.getName());
        assertEquals("string", confirmEmployeeProperty.getType());
        assertEquals("The employee to confirm the promotion", confirmEmployeeProperty.getDescription());
        assertTrue(confirmEmployeeProperty.isRequired(), "employee property should be required");

        // Verify mapping of confirm-employee-promotion
        assertNotNull(confirmEmployeeProperty.getMapping(), "Mapping should not be null");
        Mapping mapping = confirmEmployeeProperty.getMapping();
        assertEquals("header", mapping.getType(), "Mapping type should be header");
        assertEquals("EMPLOYEE", mapping.getName(), "Mapping name should be EMPLOYEE");

        // Verify the resources map
        assertNotNull(mcpSpec.getMcp().getResources(), "Resources wrapper should not be null");
        Map<String, Definition> resources = mcpSpec.getMcp().getResources().getDefinitions();
        assertNotNull(resources, "Resources map should not be null");
        assertEquals(1, resources.size(), "Should have 1 resource defined");

        // Verify employee-performance-history resource
        assertTrue(
                resources.containsKey("employee-performance-history"),
                "Should contain employee-performance-history resource");
        Definition performanceHistory = resources.get("employee-performance-history");
        assertNotNull(performanceHistory, "employee-performance-history should not be null");
        assertEquals("Obtain the employee performance history", performanceHistory.getDescription());
        assertEquals(
                "direct:employee-performance-history",
                performanceHistory.getRoute().getUri());

        // Verify properties of employee-performance-history
        assertNotNull(performanceHistory.getProperties(), "Properties should not be null");
        assertEquals(1, performanceHistory.getProperties().size(), "Should have 1 property");

        Property performanceEmployeeProperty =
                performanceHistory.getProperties().get(0);
        assertEquals("employee", performanceEmployeeProperty.getName());
        assertEquals("string", performanceEmployeeProperty.getType());
        assertEquals("The employee to obtain the performance history", performanceEmployeeProperty.getDescription());
        assertTrue(performanceEmployeeProperty.isRequired(), "employee property should be required");

        // Verify mapping of employee-performance-history
        assertNotNull(performanceEmployeeProperty.getMapping(), "Mapping should not be null");
        Mapping resourceMapping = performanceEmployeeProperty.getMapping();
        assertEquals("header", resourceMapping.getType(), "Mapping type should be header");
        assertEquals("EMPLOYEE", resourceMapping.getName(), "Mapping name should be EMPLOYEE");
    }
}
