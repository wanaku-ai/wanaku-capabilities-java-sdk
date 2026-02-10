package ai.wanaku.capabilities.sdk.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReservedArgumentNamesTest {

    @Test
    void bodyConstantHasCorrectValue() {
        assertEquals("wanaku_body", ReservedArgumentNames.BODY);
    }

    @Test
    void metadataPrefixConstantHasCorrectValue() {
        assertEquals("wanaku_meta_", ReservedArgumentNames.METADATA_PREFIX);
    }

    @Test
    void metadataPrefixCanBeUsedToIdentifyMetadataArguments() {
        String argumentName = "wanaku_meta_contextId";
        String expectedHeaderName = "contextId";

        assertTrue(argumentName.startsWith(ReservedArgumentNames.METADATA_PREFIX));
        String headerName = argumentName.substring(ReservedArgumentNames.METADATA_PREFIX.length());
        assertEquals(expectedHeaderName, headerName);
    }

    @Test
    void metadataPrefixWorksWithVariousHeaderNames() {
        String[][] testCases = {
            {"wanaku_meta_userId", "userId"},
            {"wanaku_meta_requestId", "requestId"},
            {"wanaku_meta_X-Custom-Header", "X-Custom-Header"},
        };

        for (String[] testCase : testCases) {
            String argumentName = testCase[0];
            String expectedHeader = testCase[1];

            assertTrue(argumentName.startsWith(ReservedArgumentNames.METADATA_PREFIX));
            String actualHeader = argumentName.substring(ReservedArgumentNames.METADATA_PREFIX.length());
            assertEquals(expectedHeader, actualHeader);
        }
    }
}
