package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceCatalogExtractorTest {

    @TempDir
    Path tempDir;

    @Test
    void testExtractRoutesAndRules() throws Exception {
        String base64Zip = createTestZip("test-catalog", "sys1");
        Map<ResourceType, Path> result = ServiceCatalogExtractor.extract(base64Zip, "sys1", tempDir);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(ResourceType.ROUTES_REF));
        assertTrue(result.containsKey(ResourceType.RULES_REF));
        assertTrue(Files.exists(result.get(ResourceType.ROUTES_REF)));
        assertTrue(Files.exists(result.get(ResourceType.RULES_REF)));

        String routesContent = Files.readString(result.get(ResourceType.ROUTES_REF));
        assertEquals("# Routes for sys1", routesContent);
    }

    @Test
    void testExtractWithDependencies() throws Exception {
        String base64Zip = createTestZipWithDeps("test-catalog", "sys1");
        Map<ResourceType, Path> result = ServiceCatalogExtractor.extract(base64Zip, "sys1", tempDir);

        assertEquals(3, result.size());
        assertTrue(result.containsKey(ResourceType.DEPENDENCY_REF));
        assertTrue(Files.exists(result.get(ResourceType.DEPENDENCY_REF)));
    }

    @Test
    void testExtractUnknownSystem() {
        String base64Zip = createTestZip("test-catalog", "sys1");
        assertThrows(WanakuException.class, () -> ServiceCatalogExtractor.extract(base64Zip, "nonexistent", tempDir));
    }

    @Test
    void testExtractInvalidBase64() {
        assertThrows(
                WanakuException.class, () -> ServiceCatalogExtractor.extract("not-valid-base64!@#", "sys1", tempDir));
    }

    private String createTestZip(String name, String... systems) {
        return createTestZipInternal(name, false, systems);
    }

    private String createTestZipWithDeps(String name, String... systems) {
        return createTestZipInternal(name, true, systems);
    }

    private String createTestZipInternal(String name, boolean includeDeps, String... systems) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                Properties props = new Properties();
                props.setProperty("catalog.name", name);
                props.setProperty("catalog.description", "Test catalog");
                props.setProperty("catalog.services", String.join(",", systems));

                for (String sys : systems) {
                    String routesPath = sys + "/" + sys + ".camel.yaml";
                    String rulesPath = sys + "/" + sys + ".wanaku-rules.yaml";
                    props.setProperty("catalog.routes." + sys, routesPath);
                    props.setProperty("catalog.rules." + sys, rulesPath);

                    zos.putNextEntry(new ZipEntry(routesPath));
                    zos.write(("# Routes for " + sys).getBytes());
                    zos.closeEntry();

                    zos.putNextEntry(new ZipEntry(rulesPath));
                    zos.write(("# Rules for " + sys).getBytes());
                    zos.closeEntry();

                    if (includeDeps) {
                        String depsPath = sys + "/" + sys + ".dependencies.txt";
                        props.setProperty("catalog.dependencies." + sys, depsPath);

                        zos.putNextEntry(new ZipEntry(depsPath));
                        zos.write(("camel:camel-http:" + sys).getBytes());
                        zos.closeEntry();
                    }
                }

                zos.putNextEntry(new ZipEntry("index.properties"));
                ByteArrayOutputStream propsOut = new ByteArrayOutputStream();
                props.store(propsOut, null);
                zos.write(propsOut.toByteArray());
                zos.closeEntry();
            }
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
