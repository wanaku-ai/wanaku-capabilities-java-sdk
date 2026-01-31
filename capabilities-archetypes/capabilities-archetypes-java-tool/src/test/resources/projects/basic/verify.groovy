/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File

// Verify that the project was generated correctly
// The archetype plugin generates projects in the 'project' subdirectory
def projectDir = new File(basedir, "project/test-tool")

// Check that the project directory exists
assert projectDir.exists() : "Project directory was not created"
assert projectDir.isDirectory() : "Project path is not a directory"

// Check pom.xml exists
def pomFile = new File(projectDir, "pom.xml")
assert pomFile.exists() : "pom.xml was not created"
assert pomFile.isFile() : "pom.xml is not a file"

// Verify pom.xml content
def pomContent = pomFile.text
assert pomContent.contains("ai.wanaku.test") : "pom.xml does not contain correct groupId"
assert pomContent.contains("test-tool") : "pom.xml does not contain correct artifactId"
assert pomContent.contains("capabilities-bom") : "pom.xml does not reference capabilities-bom"
assert pomContent.contains("capabilities-api") : "pom.xml does not include capabilities-api dependency"
assert pomContent.contains("capabilities-common") : "pom.xml does not include capabilities-common dependency"
assert pomContent.contains("capabilities-discovery") : "pom.xml does not include capabilities-discovery dependency"
assert pomContent.contains("capabilities-exchange") : "pom.xml does not include capabilities-exchange dependency"
assert pomContent.contains("capabilities-runtime") : "pom.xml does not include capabilities-runtime dependency"
assert pomContent.contains("capabilities-config-provider-api") : "pom.xml does not include capabilities-config-provider-api dependency"
assert pomContent.contains("capabilities-config-provider-file") : "pom.xml does not include capabilities-config-provider-file dependency"

// Verify correct dependency versions
assert pomContent.contains("<slf4j.version>2.0.17</slf4j.version>") : "pom.xml has outdated slf4j version"
assert pomContent.contains("<log4j.version>2.25.2</log4j.version>") : "pom.xml has outdated log4j version"

// Check source directory structure
def srcMainJava = new File(projectDir, "src/main/java/ai/wanaku/test")
assert srcMainJava.exists() : "Source directory was not created"
assert srcMainJava.isDirectory() : "Source path is not a directory"

// Check that Java files were generated
def appFile = new File(srcMainJava, "App.java")
assert appFile.exists() : "App.java was not created"
assert appFile.isFile() : "App.java is not a file"

def appToolFile = new File(srcMainJava, "AppTool.java")
assert appToolFile.exists() : "AppTool.java was not created"
assert appToolFile.isFile() : "AppTool.java is not a file"

def provisionBaseFile = new File(srcMainJava, "ProvisionBase.java")
assert provisionBaseFile.exists() : "ProvisionBase.java was not created"
assert provisionBaseFile.isFile() : "ProvisionBase.java is not a file"

// Verify App.java has correct package and imports
def appContent = appFile.text
assert appContent.contains("package ai.wanaku.test;") : "App.java has wrong package"
assert appContent.contains("import ai.wanaku.capabilities.sdk.api.discovery.RegistrationManager;") : "App.java has wrong RegistrationManager import"
assert appContent.contains("import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;") : "App.java has wrong ServiceTarget import"
assert appContent.contains("import ai.wanaku.capabilities.sdk.common.config.DefaultServiceConfig;") : "App.java has wrong DefaultServiceConfig import"
assert appContent.contains("import ai.wanaku.capabilities.sdk.common.serializer.JacksonSerializer;") : "App.java has wrong JacksonSerializer import"
assert !appContent.contains("import java.util.concurrent.Callable;\nimport java.util.concurrent.Callable;") : "App.java has duplicate imports"

// Verify ProvisionBase.java has correct imports
def provisionContent = provisionBaseFile.text
assert provisionContent.contains("package ai.wanaku.test;") : "ProvisionBase.java has wrong package"
assert provisionContent.contains("import ai.wanaku.capabilities.sdk.config.provider.api.ConfigProvisioner;") : "ProvisionBase.java has wrong ConfigProvisioner import"
assert provisionContent.contains("import ai.wanaku.capabilities.sdk.config.provider.api.ProvisionedConfig;") : "ProvisionBase.java has wrong ProvisionedConfig import"

// Check that the target directory exists (build was successful)
def targetDir = new File(projectDir, "target")
assert targetDir.exists() : "Target directory does not exist - build may have failed"

// Check that the fat JAR was created
def jarFile = new File(targetDir, "test-tool-app.jar")
assert jarFile.exists() : "Fat JAR was not created: ${jarFile.absolutePath}"

println "All archetype verification checks passed!"
