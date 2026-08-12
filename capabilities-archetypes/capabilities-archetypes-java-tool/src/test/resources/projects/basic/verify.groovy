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
assert pomContent.contains("capabilities-runtime") : "pom.xml does not include capabilities-runtime dependency"

// Verify correct dependency versions
assert pomContent.contains("<slf4j.version>2.0.18</slf4j.version>") : "pom.xml has outdated slf4j version"
assert pomContent.contains("<log4j.version>2.26.0</log4j.version>") : "pom.xml has outdated log4j version"


println "All archetype verification checks passed!"
