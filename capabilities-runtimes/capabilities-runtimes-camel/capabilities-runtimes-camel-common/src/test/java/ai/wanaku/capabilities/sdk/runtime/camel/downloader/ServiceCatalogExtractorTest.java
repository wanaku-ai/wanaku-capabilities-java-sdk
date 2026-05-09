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
    void testExtractWithServiceProperties() throws Exception {
        String base64Zip = createTestZipWithProperties("test-catalog", "sys1");
        Map<ResourceType, Path> result = ServiceCatalogExtractor.extract(base64Zip, "sys1", tempDir);

        assertTrue(result.containsKey(ResourceType.PROPERTIES_REF));
        assertTrue(Files.exists(result.get(ResourceType.PROPERTIES_REF)));

        Properties extracted = new Properties();
        try (var is = Files.newInputStream(result.get(ResourceType.PROPERTIES_REF))) {
            extracted.load(is);
        }
        assertEquals("test-api-key", extracted.getProperty("forage.tavily.api.key"));
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

    @Test
    void testExtractMissingRoutesProperty() {
        String base64Zip = createZipWithMissingProperty("catalog.routes.sys1");
        WanakuException ex =
                assertThrows(WanakuException.class, () -> ServiceCatalogExtractor.extract(base64Zip, "sys1", tempDir));
        assertTrue(ex.getMessage().contains("catalog.routes.sys1"));
    }

    @Test
    void testExtractMissingRulesProperty() {
        String base64Zip = createZipWithMissingProperty("catalog.rules.sys1");
        WanakuException ex =
                assertThrows(WanakuException.class, () -> ServiceCatalogExtractor.extract(base64Zip, "sys1", tempDir));
        assertTrue(ex.getMessage().contains("catalog.rules.sys1"));
    }

    private String createTestZip(String name, String... systems) {
        return createTestZipInternal(name, false, systems);
    }

    private String createTestZipWithDeps(String name, String... systems) {
        return createTestZipInternal(name, true, systems);
    }

    private String createTestZipWithProperties(String name, String... systems) {
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

                    zos.putNextEntry(new ZipEntry(sys + "/service.properties"));
                    zos.write("forage.tavily.api.key=test-api-key\n".getBytes());
                    zos.closeEntry();
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

    private String createZipWithMissingProperty(String propertyToOmit) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                Properties props = new Properties();
                props.setProperty("catalog.name", "test-catalog");
                props.setProperty("catalog.services", "sys1");

                if (!propertyToOmit.equals("catalog.routes.sys1")) {
                    props.setProperty("catalog.routes.sys1", "sys1/sys1.camel.yaml");
                }
                if (!propertyToOmit.equals("catalog.rules.sys1")) {
                    props.setProperty("catalog.rules.sys1", "sys1/sys1.wanaku-rules.yaml");
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
