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

import java.util.Properties;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GAVTest {

    @Test
    void parseValidGav() {
        GAV gav = GAV.parse("org.apache.commons:commons-lang3:3.14.0");
        assertEquals("org.apache.commons", gav.groupId());
        assertEquals("commons-lang3", gav.artifactId());
        assertEquals("3.14.0", gav.version());
    }

    @Test
    void parseTrimsWhitespace() {
        GAV gav = GAV.parse(" org.example : my-lib : 1.0 ");
        assertEquals("org.example", gav.groupId());
        assertEquals("my-lib", gav.artifactId());
        assertEquals("1.0", gav.version());
    }

    @Test
    void parseRejectsTooFewParts() {
        assertThrows(IllegalArgumentException.class, () -> GAV.parse("org.example:my-lib"));
    }

    @Test
    void parseRejectsTooManyParts() {
        assertThrows(IllegalArgumentException.class, () -> GAV.parse("org.example:my-lib:jar:1.0"));
    }

    @Test
    void parseRejectsNull() {
        assertThrows(NullPointerException.class, () -> GAV.parse(null));
    }

    @Test
    void toStringRoundTrips() {
        GAV gav = new GAV("org.example", "my-lib", "1.0");
        assertEquals("org.example:my-lib:1.0", gav.toString());
        assertEquals(gav, GAV.parse(gav.toString()));
    }

    @Test
    void constructorRejectsNullGroupId() {
        assertThrows(NullPointerException.class, () -> new GAV(null, "a", "1"));
    }

    @Test
    void constructorRejectsNullArtifactId() {
        assertThrows(NullPointerException.class, () -> new GAV("g", null, "1"));
    }

    @Test
    void constructorRejectsNullVersion() {
        assertThrows(NullPointerException.class, () -> new GAV("g", "a", null));
    }

    @Test
    void parseTwoPartWithGroupIdKey() {
        Properties props = new Properties();
        props.setProperty("org.example", "2.0.0");

        GAV gav = GAV.parse("org.example:my-lib", props);
        assertEquals("org.example", gav.groupId());
        assertEquals("my-lib", gav.artifactId());
        assertEquals("2.0.0", gav.version());
    }

    @Test
    void parseTwoPartWithSpecificKey() {
        Properties props = new Properties();
        props.setProperty("org.example:my-lib", "3.0.0");

        GAV gav = GAV.parse("org.example:my-lib", props);
        assertEquals("org.example", gav.groupId());
        assertEquals("my-lib", gav.artifactId());
        assertEquals("3.0.0", gav.version());
    }

    @Test
    void parseTwoPartSpecificKeyTakesPrecedence() {
        Properties props = new Properties();
        props.setProperty("org.example", "2.0.0");
        props.setProperty("org.example:my-lib", "3.0.0");

        GAV gav = GAV.parse("org.example:my-lib", props);
        assertEquals("3.0.0", gav.version());
    }

    @Test
    void parseTwoPartMissingVersionThrows() {
        Properties props = new Properties();
        assertThrows(IllegalArgumentException.class, () -> GAV.parse("org.example:my-lib", props));
    }

    @Test
    void parseThreePartWithPropertiesIgnoresLookup() {
        Properties props = new Properties();
        GAV gav = GAV.parse("org.example:my-lib:1.0", props);
        assertEquals("1.0", gav.version());
    }
}
